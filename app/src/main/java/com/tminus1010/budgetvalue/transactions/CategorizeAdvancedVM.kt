package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.InvalidSearchText
import com.tminus1010.budgetvalue._core.extensions.*
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.mergeCombineWithIndex
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.budgetvalue._core.models.CategoryAmountFormulaVMItem
import com.tminus1010.budgetvalue._core.models.CategoryAmountFormulas
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.replay_or_future.data.FuturesRepo
import com.tminus1010.budgetvalue.replay_or_future.data.ReplaysRepo
import com.tminus1010.budgetvalue.replay_or_future.models.BasicFuture
import com.tminus1010.budgetvalue.replay_or_future.models.BasicReplay
import com.tminus1010.budgetvalue.replay_or_future.models.IReplayOrFuture
import com.tminus1010.budgetvalue.replay_or_future.models.TotalFuture
import com.tminus1010.budgetvalue.transactions.domain.SaveTransactionDomain
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.budgetvalue.transactions.models.SearchType
import com.tminus1010.budgetvalue.transactions.models.Transaction
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.tuple.Box
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class CategorizeAdvancedVM @Inject constructor(
    private val saveTransactionDomain: SaveTransactionDomain,
    private val replaysRepo: ReplaysRepo,
    private val futuresRepo: FuturesRepo,
    private val errorSubject: Subject<Throwable>,
    private val transactionsDomain: TransactionsDomain,
) : ViewModel() {
    // # Input
    fun setup(_transaction: Transaction?, _replay: IReplayOrFuture?, categorySelectionVM: CategorySelectionVM) {
        _categorySelectionVM = categorySelectionVM
        _replayOrFuture.onNext(Box(_replay))
        transaction.onNext(Box(_transaction))
    }

    private val userCategoryAmounts = SourceHashMap<Category, BigDecimal>()
    fun userInputCA(category: Category, amount: BigDecimal) {
        if (amount.isZero)
            userCategoryAmounts.remove(category)
        else
            userCategoryAmounts[category] = amount
    }

    private val userCategoryIsPercentage = SourceHashMap<Category, Boolean>()
    fun userSwitchCategoryIsPercentage(category: Category, isPercentage: Boolean) {
        userCategoryIsPercentage[category] = isPercentage
    }

    fun userSubmitCategorization() {
        saveTransactionDomain.saveTransaction(transactionToPush.unbox)
            .andThen(_categorySelectionVM.clearSelection())
            .observe(disposables,
                onComplete = { navUp.onNext(Unit) },
                onError = { errorSubject.onNext(it) }
            )
    }

    fun userSaveReplay(name: String) {
        val replay = BasicReplay(
            name = name,
            searchTexts = listOf(transaction.unbox.description),
            categoryAmountFormulas = categoryAmountFormulas.value!!.filter { !it.value.isZero() },
            fillCategory = fillCategory.value!!,
        )
        replaysRepo.add(replay)
            .andThen(_categorySelectionVM.clearSelection())
            .observe(disposables,
                onComplete = { navUp.onNext(Unit) },
                onError = { errorSubject.onNext(it) }
            )
    }

    fun userSaveFuture(name: String) {
        Single.fromCallable {
            if (searchText.value!!.isEmpty()) InvalidSearchText("Search text was empty")
            when (searchType.value!!) {
                SearchType.DESCRIPTION -> BasicFuture(
                    name = name,
                    searchText = searchText.value!!,
                    categoryAmountFormulas = categoryAmountFormulas.value!!.filter { !it.value.isZero() },
                    fillCategory = fillCategory.value!!,
                    isPermanent = isPermanent.value!!
                )
                SearchType.TOTAL -> TotalFuture(
                    name = name,
                    searchTotal = total.value,
                    categoryAmountFormulas = categoryAmountFormulas.value!!.filter { !it.value.isZero() },
                    fillCategory = fillCategory.value!!,
                    isPermanent = isPermanent.value!!
                )
                else -> TODO()
            }
        }
            .flatMapCompletable { future ->
                Rx.merge(
                    listOfNotNull(
                        if (future.isPermanent) transactionsDomain.applyReplayOrFutureToUncategorizedSpends(future) else null,
                        futuresRepo.add(future),
                        _categorySelectionVM.clearSelection(),
                    )
                )
            }
            .observe(disposables,
                onComplete = { navUp.onNext(Unit) },
                onError = { errorSubject.onNext(it) }
            )
    }

    fun userDeleteReplay(replayName: String) {
        replaysRepo.delete(replayName)
            .observe(disposables,
                onComplete = { navUp.onNext(Unit) },
                onError = { errorSubject.onNext(it) }
            )
    }

    private val userAutoFillCategory = BehaviorSubject.create<Category>()!!
    fun userSetCategoryForAutoFill(category: Category) {
        userAutoFillCategory.onNext(category)
    }

    private val userSearchText = BehaviorSubject.createDefault("")!!
    fun userSetSearchText(s: String) {
        userSearchText.onNext(s)
    }

    private val userTotalGuess = BehaviorSubject.createDefault(BigDecimal.ZERO)!!
    fun userSetTotalGuess(bigDecimal: BigDecimal) {
        userTotalGuess.onNext(bigDecimal)
    }

    private val userIsPermanent = BehaviorSubject.createDefault(false)!!
    fun userSetIsPermanent(boolean: Boolean) {
        userIsPermanent.onNext(boolean)
    }

    private val userSearchType = BehaviorSubject.create<SearchType>()!!
    fun userSetSearchType(searchType: SearchType) {
        userSearchType.onNext(searchType)
    }

    // # Internal
    private val transaction = BehaviorSubject.createDefault(Box<Transaction?>(null))
    private val _replayOrFuture = BehaviorSubject.createDefault(Box<IReplayOrFuture?>(null))
    private lateinit var _categorySelectionVM: CategorySelectionVM
    private val categorySelectionVM = Observable.timer(100, TimeUnit.MILLISECONDS)
        .map { _categorySelectionVM }
        .retry() // error if setup() has not yet been called.
        .replay(1).refCount()!!
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
            .nonLazyCache(disposables)

    // # Output
    val searchType =
        userSearchType
            .startWithItem(SearchType.DESCRIPTION_AND_TOTAL)
            .nonLazyCache(disposables)
    val searchText =
        Observable.merge(
            transaction.map { it.first?.description ?: "" },
            userSearchText,
        )
            .nonLazyCache(disposables)
    val total =
        Observable.merge(
            transaction.map { it.first?.amount ?: BigDecimal.ZERO },
            userTotalGuess,
        )
            .nonLazyCache(disposables)
            .cold()
    val isPermanent: Observable<Boolean> = userIsPermanent
    val replayOrFuture: Observable<Box<IReplayOrFuture?>> = _replayOrFuture!!
    val amountToCategorizeMsg =
        transaction
            .map { (transaction) -> Box(transaction?.let { "Amount to split: $${transaction.amount}" }) }
            .nonLazyCache(disposables)
    val fillCategory =
        mergeCombineWithIndex(
            userAutoFillCategory,
            Rx.combineLatest(
                _replayOrFuture,
                categorySelectionVM.flatMap { it.selectedCategories },
            ),
        ).map { (i, userAutoFillCategory, replayOrFutureAndSelectedCategories) ->
            when (i) {
                0 -> userAutoFillCategory!!
                1 -> {
                    val (replayOrFuture, selectedCategories) = replayOrFutureAndSelectedCategories!!
                    replayOrFuture.first?.fillCategory
                        ?: selectedCategories.find { it.defaultAmountFormula.isZero() }
                        ?: selectedCategories.getOrNull(0)
                        ?: CategoriesDomain.defaultCategory
                }
                else -> error("unhandled index")
            }
        }
            .distinctUntilChanged()
            .nonLazyCache(disposables)
    private val categoryAmountFormulas =
        Rx.combineLatest(
            _replayOrFuture,
            userCategoryAmountFormulas,
            categorySelectionVM.flatMap { it.selectedCategories },
        )
            .map { (replayBox, userCategoryAmountFormulas, selectedCategories) ->
                val replay = replayBox.first
                CategoryAmountFormulas(replay?.categoryAmountFormulas ?: emptyMap())
                    .plus(selectedCategories.associateWith { it.defaultAmountFormula })
                    .plus(userCategoryAmountFormulas)
            }
            .nonLazyCache(disposables)
    private val fillAmountFormula =
        Rx.combineLatest(
            categoryAmountFormulas,
            fillCategory,
            total,
        )
            .map { (categoryAmountFormulas, fillCategory, total) ->
                categoryAmountFormulas.fillIntoCategory(fillCategory, total)[fillCategory]!!
            }
            .startWithItem(AmountFormula.Value(BigDecimal.ZERO)) // This might not be necessary
            .cold()
    val categoryAmountFormulaVMItems: Observable<List<CategoryAmountFormulaVMItem>> =
        categoryAmountFormulas
            .flatMapSourceHashMap { it.itemObservableMap }
            .map { it.map { (k, v) -> CategoryAmountFormulaVMItem(k, v, fillCategory, fillAmountFormula, ::userSwitchCategoryIsPercentage, ::userInputCA) } }
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
    val defaultAmount =
        Rx.combineLatest(
            categoryAmountFormulas,
            total,
        )
            .map { (categoryAmountFormulas, total) ->
                categoryAmountFormulas.defaultAmount(total).toString()
            }!!
    val areCurrentCAsValid: Observable<Boolean> =
        categoryAmountFormulas
            .map { it.isNotEmpty() }
            .nonLazyCache(disposables)
    val navUp = PublishSubject.create<Unit>()!!
}