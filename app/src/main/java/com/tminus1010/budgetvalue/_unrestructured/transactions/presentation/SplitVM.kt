package com.tminus1010.budgetvalue._unrestructured.transactions.presentation

import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._unrestructured.replay_or_future.app.SelectCategoriesModel
import com.tminus1010.budgetvalue._unrestructured.replay_or_future.presentation.CategoryAmountFormulaVMItemsBaseVM
import com.tminus1010.budgetvalue._unrestructured.transactions.app.Transaction
import com.tminus1010.budgetvalue._unrestructured.transactions.app.interactor.SaveTransactionInteractor
import com.tminus1010.budgetvalue.all_layers.extensions.cold
import com.tminus1010.budgetvalue.all_layers.extensions.nonLazyCache
import com.tminus1010.budgetvalue.all_layers.extensions.unbox
import com.tminus1010.budgetvalue.app.CategoriesInteractor
import com.tminus1010.budgetvalue.framework.ColdObservable
import com.tminus1010.budgetvalue.ui.all_features.model.ButtonVMItem
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.tuple.Box
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class SplitVM @Inject constructor(
    private val saveTransactionInteractor: SaveTransactionInteractor,
    private val errorSubject: Subject<Throwable>,
    override val categoriesInteractor: CategoriesInteractor,
    override val selectCategoriesModel: SelectCategoriesModel,
) : CategoryAmountFormulaVMItemsBaseVM() {
    // # Input
    fun setup(_transaction: Transaction?) {
        transaction.onNext(Box(_transaction))
    }

    // TODO("Why does userSubmitCategorization not work for only 1 category?")
    fun userSubmitCategorization() {
        saveTransactionInteractor.saveTransaction(transactionToPush.unbox)
            .andThen(Completable.fromAction { runBlocking { selectCategoriesModel.clearSelection() } })
            .observe(disposables,
                onComplete = { navUp.onNext(Unit) },
                onError = { errorSubject.onNext(it) }
            )
    }

    // # Internal
    private val transaction = BehaviorSubject.createDefault(Box<Transaction?>(null))

    // # Output
    override val _totalGuess: ColdObservable<BigDecimal> =
        transaction.map { (it) -> it?.amount ?: BigDecimal.ZERO }
            .nonLazyCache(disposables)
            .cold()

    val amountToCategorizeMsg =
        transaction
            .map { (transaction) -> Box(transaction?.let { "Amount to split: $${transaction.amount}" }) }
            .nonLazyCache(disposables)
    private val transactionToPush =
        Observable.combineLatest(transaction, categoryAmountFormulas)
        { (transaction), categoryAmountFormulas ->
            Box(transaction?.categorize(categoryAmountFormulas.mapValues { it.value.calcAmount(transaction.amount) }))
        }
            .nonLazyCache(disposables)
    val defaultAmount =
        Observable.combineLatest(categoryAmountFormulas, totalGuess)
        { categoryAmountFormulas, total ->
            categoryAmountFormulas.defaultAmount(total).toString()
        }
    val areCurrentCAsValid =
        categoryAmountFormulas
            .map { it.isNotEmpty() }
            .nonLazyCache(disposables)
            .cold()
    val navUp = PublishSubject.create<Unit>()

    val buttons
        get() = listOfNotNull(
            ButtonVMItem(
                title = "Submit",
                onClick = ::userSubmitCategorization
            ),
        )
}