package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.categoryComparator
import com.tminus1010.budgetvalue._core.extensions.nonLazyCache
import com.tminus1010.budgetvalue._core.extensions.unbox
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.budgetvalue._core.models.CategoryAmountFormulas
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.replay.ReplayDomain
import com.tminus1010.budgetvalue.replay.data.ReplayRepo
import com.tminus1010.budgetvalue.replay.models.BasicReplay
import com.tminus1010.budgetvalue.replay.models.IReplayOrFuture
import com.tminus1010.budgetvalue.transactions.domain.SaveTransactionDomain
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.budgetvalue.transactions.models.Transaction
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.tuple.Box
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
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
) : ViewModel() {
    // # Input
    private val shouldLogInput = true
    fun setup(_transaction: Transaction?, _replay: IReplayOrFuture?, categorySelectionVM: CategorySelectionVM) {
        if (shouldLogInput) logz("_transaction:$_transaction _replay:$_replay categorySelectionVM:$categorySelectionVM")
        _categorySelectionVM = categorySelectionVM
        replayOrFuture.onNext(Box(_replay))
        transaction.onNext(Box(_transaction))
        userCategoryAmounts.clear()
        userCategoryIsPercentage.clear()
    }

    fun userFillIntoCategory(category: Category) {
        val amount = categoryAmountFormulas.value!!.calcFillAmount(category, transactionToPush.unbox.amount)
        if (amount.compareTo(BigDecimal.ZERO) == 0)
            userCategoryAmounts.remove(category)
        else
            userCategoryAmounts[category] = amount
    }

    fun userInputCA(category: Category, amount: BigDecimal) {
        if (amount.compareTo(BigDecimal.ZERO) == 0)
            userCategoryAmounts.remove(category)
        else
            userCategoryAmounts[category] = amount
    }

    fun userSwitchCategoryToPercentage(category: Category) {
        userCategoryIsPercentage[category] = true
    }

    fun userSwitchCategoryToNonPercentage(category: Category) {
        userCategoryIsPercentage[category] = false
    }

    fun userSubmitCategorization() {
        saveTransactionDomain.saveTransaction(transactionToPush.unbox)
            .andThen(_categorySelectionVM.clearSelection())
            .observe(disposables)
    }

    fun userSaveReplay(replayName: String, isAutoReplay: Boolean) {
        val replay = BasicReplay(
            name = replayName,
            description = transaction.unbox.description,
            categoryAmountFormulas = categoryAmountFormulas.value!!.filter { !it.value.isZero() },
            isAutoReplay = isAutoReplay,
            autoFillCategory = autoFillCategory.value!!,
        )
        Rx.merge(
            listOfNotNull(
                if (replay.isAutoReplay) replayDomain.applyReplayToAllTransactions(replay) else null,
                replayRepo.add(replay),
                _categorySelectionVM.clearSelection(),
            )
        )
            .observe(disposables, onComplete = {
                navUp.onNext(Unit)
            }, onError = {
                errorSubject.onNext(it)
            })
    }

    fun userDeleteReplay(replayName: String) {
        replayRepo.delete(replayName)
            .observe(disposables, onComplete = {
                navUp.onNext(Unit)
            }, onError = {
                errorSubject.onNext(it)
            })
    }

    fun userSetCategoryForAutoFill(category: Category) {
        userAutoFillCategory.onNext(category)
    }

    // # Internal
    private val userCategoryAmounts = SourceHashMap<Category, BigDecimal>()
    private val userCategoryIsPercentage = SourceHashMap<Category, Boolean>()
    private val userAutoFillCategory = BehaviorSubject.createDefault(CategoriesDomain.defaultCategory)!!
    private val transaction = BehaviorSubject.createDefault(Box<Transaction?>(null))
    private val replayOrFuture = BehaviorSubject.createDefault(Box<IReplayOrFuture?>(null))
    private lateinit var _categorySelectionVM: CategorySelectionVM
    private val userCategoryAmountFormulas =
        Rx.combineLatest(
            userCategoryAmounts.observable,
            userCategoryIsPercentage.observable,
        )
            .map { (userCategoryAmounts, userCategoryIsPercentage) ->
                (userCategoryAmounts.keys + userCategoryIsPercentage.keys)
                    .associateWith {
                        if (userCategoryIsPercentage[it] ?: false)
                            AmountFormula.Percentage(userCategoryAmounts[it] ?: BigDecimal.ZERO)
                        else
                            AmountFormula.Value(userCategoryAmounts[it] ?: BigDecimal.ZERO)
                    }
            }

    // # Output
    val amountToCategorizeMsg =
        transaction
            .map { transactionBox ->
                val transaction = transactionBox.first
                Box(transaction?.let { "Amount to split: $${transaction.amount}" })
            }
            .nonLazyCache(disposables)
    val autoFillCategory: Observable<Category> =
        Observable.merge(
            userAutoFillCategory,
            replayOrFuture.map { it.first?.autoFillCategory ?: CategoriesDomain.defaultCategory },
        )
            .distinctUntilChanged()
            .nonLazyCache(disposables)
    private val categoryAmountFormulas =
        Rx.combineLatest(
            transaction,
            autoFillCategory,
            replayOrFuture,
            userCategoryAmountFormulas
        )
            .map { (transactionBox, autoFillCategory, replayBox, userCategoryAmountFormulas) ->
                val transaction = transactionBox.first
                val replay = replayBox.first
                CategoryAmountFormulas(replay?.categoryAmountFormulas ?: emptyMap())
                    .plus(userCategoryAmountFormulas.filter { !it.value.isZero() })
                    .run { if (transaction != null) fillIntoCategory(autoFillCategory, transaction.amount) else this }
            }
            .startWithItem(CategoryAmountFormulas())
            .nonLazyCache(disposables)
    val categoryAmountFormulasToShow =
        Rx.combineLatest(
            categoryAmountFormulas,
            userCategoryAmountFormulas,
            Observable.timer(300, TimeUnit.MILLISECONDS).map { _categorySelectionVM }.retry().flatMap { it.selectedCategories }
        )
            .map { (categoryAmountFormulas, userCategoryAmountFormulas, selectedCategories) ->
                userCategoryAmountFormulas
                    .plus(selectedCategories.associateWith { AmountFormula.Value(BigDecimal.ZERO) })
                    .plus(categoryAmountFormulas)
            }
            .map { it.toSortedMap(categoryComparator) }
            .nonLazyCache(disposables)
    private val transactionToPush =
        Rx.combineLatest(
            transaction,
            categoryAmountFormulas,
        )
            .map { (transactionBox, categoryAmountFormulas) ->
                val transaction = transactionBox.first
                Box(transaction?.categorize(categoryAmountFormulas.mapValues { it.value.calcAmount(transaction.amount) }))
            }
            .nonLazyCache(disposables)
    val defaultAmount: Observable<Box<String?>> =
        transactionToPush
            .map { Box(it.first?.defaultAmount?.toString()) }
    val areCurrentCAsValid: Observable<Boolean> =
        categoryAmountFormulas
            .map { it.isNotEmpty() }
            .nonLazyCache(disposables)
    val navUp = PublishSubject.create<Unit>()!!
}