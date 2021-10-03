package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.all.extensions.cold
import com.tminus1010.budgetvalue._core.all.extensions.nonLazyCache
import com.tminus1010.budgetvalue._core.all.extensions.unbox
import com.tminus1010.budgetvalue._core.middleware.ColdObservable
import com.tminus1010.budgetvalue._core.middleware.Toaster
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.ICategoryParser
import com.tminus1010.budgetvalue.replay_or_future.CategoryAmountFormulaVMItemsBaseVM
import com.tminus1010.budgetvalue.replay_or_future.data.ReplaysRepo
import com.tminus1010.budgetvalue.replay_or_future.models.BasicReplay
import com.tminus1010.budgetvalue.transactions.domain.SaveTransactionDomain
import com.tminus1010.budgetvalue.transactions.app.TransactionsInteractor
import com.tminus1010.budgetvalue.transactions.models.Transaction
import com.tminus1010.tmcommonkotlin.misc.generateUniqueID
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.tuple.Box
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class SplitVM @Inject constructor(
    private val saveTransactionDomain: SaveTransactionDomain,
    private val replaysRepo: ReplaysRepo,
    private val errorSubject: Subject<Throwable>,
    override val categoryParser: ICategoryParser,
    private val transactionsInteractor: TransactionsInteractor,
    private val toaster: Toaster,
) : CategoryAmountFormulaVMItemsBaseVM() {
    // # Input
    fun setup(_transaction: Transaction?, categorySelectionVM: CategorySelectionVM) {
        this.categorySelectionVM = categorySelectionVM
        _categorySelectionVM = categorySelectionVM
        transaction.onNext(Box(_transaction))
    }

    // TODO("Why does userSubmitCategorization not work for only 1 category?")
    fun userSubmitCategorization() {
        saveTransactionDomain.saveTransaction(transactionToPush.unbox)
            .andThen(_categorySelectionVM.clearSelection())
            .observe(disposables,
                onComplete = { navUp.onNext(Unit) },
                onError = { errorSubject.onNext(it) }
            )
    }

    fun userSubmitCategorizationForAllUncategorized() {
        transactionsInteractor.applyReplayOrFutureToUncategorizedSpends(
            BasicReplay(
                generateUniqueID(),
                listOf(transactionToPush.unbox.description),
                categoryAmountFormulas.value!!.filter { !it.value.isZero() },
                fillCategory.unbox
            )
        ).subscribeBy(
            onSuccess = { toaster.toast("$it transactions categorized"); _categorySelectionVM.clearSelection().subscribe(); navUp.onNext(Unit) }
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

    // # Internal
    private val transaction = BehaviorSubject.createDefault(Box<Transaction?>(null))
    private lateinit var _categorySelectionVM: CategorySelectionVM

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
        }!!
    val areCurrentCAsValid =
        categoryAmountFormulas
            .map { it.isNotEmpty() }
            .nonLazyCache(disposables)
            .cold()
    val navUp = PublishSubject.create<Unit>()!!
    val saveReplayDialogBox = PublishSubject.create<String>()!!

    val buttons
        get() = listOfNotNull(
            ButtonVMItem(
                title = "Save Replay",
                userClick = {
                    saveReplayDialogBox.onNext(
                        categoryAmountFormulas.value!!.map { (category, amountFormula) ->
                            if (category != fillCategory.value.first)
                                amountFormula.toDisplayStr2() + " " + category.name
                            else
                                category.name
                        }.joinToString(", ")
                    )
                }
            ),
            ButtonVMItem(
                title = "Submit for all Uncategorized with matching description",
                userClick = ::userSubmitCategorizationForAllUncategorized
            ),
            ButtonVMItem(
                title = "Submit",
                userClick = ::userSubmitCategorization
            ),
        )
}