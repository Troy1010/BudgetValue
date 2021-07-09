package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.extensions.copy
import com.tminus1010.budgetvalue._core.extensions.divertErrors
import com.tminus1010.budgetvalue._core.extensions.nonLazyCache
import com.tminus1010.budgetvalue.replay.AutoReplayDomain
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.domain.SaveTransactionDomain
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.unbox
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.tuple.Box
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CategorizeAdvancedVM @Inject constructor(
    errorSubject: Subject<Throwable>,
    private val saveTransactionDomain: SaveTransactionDomain,
    transactionsDomain: TransactionsDomain,
    private val autoReplayDomain: AutoReplayDomain
) : ViewModel() {
    // # Input
    fun userFillIntoCategory(category: Category) {
        intents.onNext(Intents.FillIntoCategory(category))
    }

    fun userInputCA(category: Category, amount: BigDecimal) {
        intents.onNext(Intents.Add(category, amount))
    }

    fun userClearCA() {
        intents.onNext(Intents.Clear)
    }

    fun userSaveTransaction() {
        transactionToPush.take(1)
            .flatMapCompletable { saveTransactionDomain.saveTransaction(it) }
            .andThen(_categorySelectionVM.clearSelection())
            .observe(disposables)
    }

    fun userBeginAutoReplay() {
        transactionToPush.take(1)
            .flatMapCompletable { autoReplayDomain.addAutoReplay(it.description, it.categoryAmounts) }
            .andThen(_categorySelectionVM.clearSelection())
            .observe(disposables)
    }

    fun setup(categoryAmounts: Map<Category, BigDecimal>?, categorySelectionVM: CategorySelectionVM) {
        _categorySelectionVM = categorySelectionVM
        transactionToPush.take(1)
            .observe(disposables) {
                if (categoryAmounts != null) userClearCA()
                _categorySelectionVM.selectedCategories.value!!
                    .filter { it !in transactionToPush.value!!.categoryAmounts.keys }
                    .forEach { userInputCA(it, it.defaultAmount) }
                categoryAmounts
                    ?.forEach { userInputCA(it.key, it.value) }
            }
    }

    // # Internal
    private val intents = PublishSubject.create<Intents>()

    private sealed class Intents {
        object Clear : Intents()
        class Add(val category: Category, val amount: BigDecimal) : Intents()
        class FillIntoCategory(val category: Category) : Intents()
    }

    private val firstTransactionBox =
        transactionsDomain.uncategorizedSpends
            .map { Box(it.getOrNull(0)) }
            .nonLazyCache(disposables)
    private lateinit var _categorySelectionVM: CategorySelectionVM

    // # Output
    val transactionToPush = firstTransactionBox
        .unbox()
        .switchMap {
            intents
                .scan(it) { acc, v ->
                    when (v) {
                        Intents.Clear -> acc.categorize(emptyMap())
                        is Intents.Add -> acc.categorize(acc.categoryAmounts.copy(v.category to v.amount))
                        is Intents.FillIntoCategory -> acc.categorize(v.category)
                    }
                }
        }
        .nonLazyCache(disposables)
    val defaultAmount = transactionToPush
        .map { it.defaultAmount.toString() }
        .divertErrors(errorSubject)
}