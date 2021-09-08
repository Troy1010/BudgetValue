package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.extensions.cold
import com.tminus1010.budgetvalue._core.extensions.nonLazyCache
import com.tminus1010.budgetvalue._core.extensions.unbox
import com.tminus1010.budgetvalue._core.middleware.ColdObservable
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.models.CategoryAmountFormulas
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.ICategoryParser
import com.tminus1010.budgetvalue.replay_or_future.CategoryAmountFormulaVMItemsBaseVM
import com.tminus1010.budgetvalue.replay_or_future.data.ReplaysRepo
import com.tminus1010.budgetvalue.replay_or_future.models.BasicReplay
import com.tminus1010.budgetvalue.replay_or_future.models.IReplayOrFuture
import com.tminus1010.budgetvalue.transactions.domain.SaveTransactionDomain
import com.tminus1010.budgetvalue.transactions.models.Transaction
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.tuple.Box
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CategorizeAdvancedVM @Inject constructor(
    private val saveTransactionDomain: SaveTransactionDomain,
    private val replaysRepo: ReplaysRepo,
    private val errorSubject: Subject<Throwable>,
    override val categoryParser: ICategoryParser,
) : CategoryAmountFormulaVMItemsBaseVM() {
    // # Input
    fun setup(_transaction: Transaction?, _replay: IReplayOrFuture?, categorySelectionVM: CategorySelectionVM) {
        this.categorySelectionVM = categorySelectionVM
        _categorySelectionVM = categorySelectionVM
        _replayOrFuture.onNext(Box(_replay))
        transaction.onNext(Box(_transaction))
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
            fillCategory = _fillCategory.value.first!!,
        )
        replaysRepo.add(replay)
            .andThen(_categorySelectionVM.clearSelection())
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

    // # Internal
    private val transaction = BehaviorSubject.createDefault(Box<Transaction?>(null))
    private val _replayOrFuture = BehaviorSubject.createDefault(Box<IReplayOrFuture?>(null))
    private lateinit var _categorySelectionVM: CategorySelectionVM

    // # Output
    override val _totalGuess: ColdObservable<BigDecimal> =
        transaction.map { (it) -> it?.amount ?: BigDecimal.ZERO }
            .nonLazyCache(disposables)
            .cold()

    val replayOrFuture: Observable<Box<IReplayOrFuture?>> = _replayOrFuture!!
    val amountToCategorizeMsg =
        transaction
            .map { (transaction) -> Box(transaction?.let { "Amount to split: $${transaction.amount}" }) }
            .nonLazyCache(disposables)

    override val _fillCategory =
        Observable.combineLatest(super._fillCategory, replayOrFuture)
        { (fillCategory), (replayOrFuture) ->
            Box(fillCategory ?: replayOrFuture?.fillCategory)
        }
            .cold()

    override val _categoryAmountFormulas =
        Observable.combineLatest(super._categoryAmountFormulas, replayOrFuture)
        { categoryAmountFormulas, (replayOrFuture) ->
            CategoryAmountFormulas(replayOrFuture?.categoryAmountFormulas
                ?.plus(categoryAmountFormulas)
                ?: categoryAmountFormulas)
        }
            .cold()
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
        Observable.combineLatest(categoryAmountFormulas, totalGuess)
        { categoryAmountFormulas, total ->
            categoryAmountFormulas.defaultAmount(total).toString()
        }!!
    val areCurrentCAsValid: Observable<Boolean> =
        categoryAmountFormulas
            .map { it.isNotEmpty() }
            .nonLazyCache(disposables)
    val navUp = PublishSubject.create<Unit>()!!
}