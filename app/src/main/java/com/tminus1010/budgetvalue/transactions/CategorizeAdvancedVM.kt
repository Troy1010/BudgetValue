package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.extensions.copy
import com.tminus1010.budgetvalue._core.extensions.nonLazyCache
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.replay.ReplayDomain
import com.tminus1010.budgetvalue.replay.data.ReplayRepo
import com.tminus1010.budgetvalue.replay.models.BasicReplay
import com.tminus1010.budgetvalue.transactions.domain.SaveTransactionDomain
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.unbox
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CategorizeAdvancedVM @Inject constructor(
    private val saveTransactionDomain: SaveTransactionDomain,
    transactionsDomain: TransactionsDomain,
    private val replayDomain: ReplayDomain,
    private val replayRepo: ReplayRepo,
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

    fun userSubmitCategorization() {
        transactionToPush.take(1)
            .flatMapCompletable { saveTransactionDomain.saveTransaction(it) }
            .andThen(_categorySelectionVM.clearSelection())
            .observe(disposables)
    }

    fun userSaveAutoReplay(replayName: String) {
        val replay = BasicReplay(
            name = replayName,
            description = transactionToPush.value!!.description,
            categoryAmounts = transactionToPush.value!!.categoryAmounts,
            isAutoReplay = true
        )
        Rx.merge(
            replayRepo.add(replay),
            replayDomain.applyReplayToAllTransactions(replay),
            _categorySelectionVM.clearSelection(),
        ).observe(disposables)
    }

    fun userSaveReplay(replayName: String) {
        replayRepo.add(
            BasicReplay(
                name = replayName,
                description = transactionToPush.value!!.description,
                categoryAmounts = transactionToPush.value!!.categoryAmounts,
                isAutoReplay = false
            )
        ).observe(disposables)
    }

    fun userDeleteReplay(replayName: String) {
        replayRepo.delete(replayName).observe(disposables)
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

    private lateinit var _categorySelectionVM: CategorySelectionVM

    // # Output
    val replays = replayRepo.fetchReplays()
    val transactionToPush = transactionsDomain.firstUncategorizedSpend
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
    val defaultAmount: Observable<String> =
        transactionToPush
            .map { it.defaultAmount.toString() }
}