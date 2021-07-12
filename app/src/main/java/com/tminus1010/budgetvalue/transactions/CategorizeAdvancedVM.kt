package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.categoryComparator
import com.tminus1010.budgetvalue._core.extensions.copy
import com.tminus1010.budgetvalue._core.extensions.nonLazyCache
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.replay.ReplayDomain
import com.tminus1010.budgetvalue.replay.data.ReplayRepo
import com.tminus1010.budgetvalue.replay.models.BasicReplay
import com.tminus1010.budgetvalue.replay.models.IReplay
import com.tminus1010.budgetvalue.transactions.domain.SaveTransactionDomain
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.unbox
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.tuple.Box
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class CategorizeAdvancedVM @Inject constructor(
    private val saveTransactionDomain: SaveTransactionDomain,
    private val replayDomain: ReplayDomain,
    private val replayRepo: ReplayRepo,
    private val errorSubject: Subject<Throwable>,
    transactionsDomain: TransactionsDomain,
) : ViewModel() {
    // # Input
    fun userFillIntoCategory(category: Category) {
        userCategoryAmounts[category] = amount.value!! - categoryAmounts.value!!.values.sum()
    }

    fun userInputCA(category: Category, amount: BigDecimal) {
        userCategoryAmounts[category] = amount
    }

    fun userSubmitCategorization() {
        transactionToPush.take(1)
            .flatMapCompletable { saveTransactionDomain.saveTransaction(it) }
            .andThen(_categorySelectionVM.clearSelection())
            .observe(disposables)
    }

    fun userSaveReplay(replayName: String, isAutoReplay: Boolean) {
        Single.fromCallable {
            BasicReplay(
                name = replayName,
                description = transactionToPush.value!!.description,
                categoryAmounts = categoryAmounts.value!!.filter { it.value.compareTo(BigDecimal.ZERO) != 0 },
                isAutoReplay = isAutoReplay,
                autoFillCategory = autoFillCategory.value!!
            )
        }.subscribeOn(Schedulers.io())
            .flatMapCompletable { replay ->
                Rx.merge(
                    listOfNotNull(
                        if (isAutoReplay) replayDomain.applyReplayToAllTransactions(replay) else null,
                        replayRepo.add(replay),
                        _categorySelectionVM.clearSelection(),
                    )
                )
            }
            .observe(disposables, onComplete = {
                navUp.onNext(Unit)
            }, onError = {
                errorSubject.onNext(it)
            })
    }

    fun userDeleteReplay(replayName: String) {
        replayRepo.delete(replayName).observe(disposables)
    }

    fun userSetCategoryForAutoFill(category: Category) {
        userAutoFillCategory.onNext(category)
    }

    fun setup(_replay: IReplay?, categorySelectionVM: CategorySelectionVM) {
        _categorySelectionVM = categorySelectionVM
        replay.onNext(Box(_replay))
        userCategoryAmounts.clear()
    }

    // # Internal
    private var userCategoryAmounts = SourceHashMap<Category, BigDecimal>()
    private val userAutoFillCategory = BehaviorSubject.createDefault(CategoriesDomain.defaultCategory)!!
    private var replay = BehaviorSubject.createDefault<Box<IReplay?>>(Box(null))

    private lateinit var _categorySelectionVM: CategorySelectionVM
    private val amount =
        transactionsDomain.firstUncategorizedSpend
            .unbox()
            .map { it.amount }
            .nonLazyCache(disposables)

    // # Output
    val replays = replayRepo.fetchReplays()
    val autoFillCategory: Observable<Category> =
        Observable.merge(
            userAutoFillCategory,
            replay.map { it.first?.autoFillCategory ?: CategoriesDomain.defaultCategory },
        )
            .distinctUntilChanged()
            .nonLazyCache(disposables)
    val categoryAmounts =
        Rx.combineLatest(
            transactionsDomain.firstUncategorizedSpend.unbox(),
            autoFillCategory,
            amount,
            Observable.timer(300, TimeUnit.MILLISECONDS).flatMap { _categorySelectionVM.selectedCategories }.retry(),
            replay,
            userCategoryAmounts.observable,
        )
            .map { (transaction, autoFillCategory, amount, selectedCategories, replay, userInputCategoryAmounts) ->
                (replay.first?.autoFillCategory?.let { selectedCategories + it } ?: selectedCategories).associateWith { BigDecimal.ZERO }
                    .plus(replay.first?.categorize(transaction)?.categoryAmounts ?: emptyMap())
                    .plus(userInputCategoryAmounts)
                    .let {
                        if (autoFillCategory == CategoriesDomain.defaultCategory)
                            it
                        else
                            it
                                .filter { it.key != autoFillCategory }
                                .let { categoryAmounts ->
                                    categoryAmounts.copy(autoFillCategory to amount - categoryAmounts.values.sum())
                                }
                    }
            }
            .startWithItem(emptyMap<Category, BigDecimal>())
            .map { it.toSortedMap(categoryComparator) }
            .nonLazyCache(disposables)
    val transactionToPush =
        Rx.combineLatest(
            transactionsDomain.firstUncategorizedSpend.unbox(),
            categoryAmounts,
        )
            .map { (transaction, categoryAmounts) ->
                transaction.categorize(categoryAmounts.filter { it.value.compareTo(BigDecimal.ZERO) != 0 })
            }
            .nonLazyCache(disposables)
    val defaultAmount: Observable<String> =
        transactionToPush
            .map { it.defaultAmount.toString() }
    val navUp = PublishSubject.create<Unit>()!!
    val areCurrentCAsValid: Observable<Boolean> =
        transactionToPush
            .map { it.categoryAmounts != emptyMap<Category, BigDecimal>() }
            .nonLazyCache(disposables)
}