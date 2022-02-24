package com.tminus1010.budgetvalue.transactions.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue._core.presentation.model.TextVMItem
import com.tminus1010.budgetvalue.transactions.app.Transaction
import com.tminus1010.budgetvalue.transactions.app.interactor.SaveTransactionInteractor
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@HiltViewModel
class TransactionVM @Inject constructor(
    private val saveTransactionInteractor: SaveTransactionInteractor,
) : ViewModel() {
    // # Setup
    val transaction = BehaviorSubject.create<Transaction>()

    // # User Intents
    fun userClearTransaction() {
        saveTransactionInteractor.saveTransaction(transaction.value!!.categorize(emptyMap()))
            .andThen(Completable.fromAction { navUp.onNext(Unit) })
            .observe(disposables)
    }

    // # Events
    val navUp = PublishSubject.create<Unit>()
    val toast = transaction.filter { it.categoryAmounts.isEmpty() }.map { "This transaction is empty" }

    // # State
    val upperRecipeGrid =
        transaction.map {
            listOf(
                listOf(
                    TextVMItem(it.date.toDisplayStr(), backgroundColor = if (it.isCategorized) null else R.attr.colorSecondary),
                    TextVMItem(it.defaultAmount.toString(), backgroundColor = if (it.isCategorized) null else R.attr.colorSecondary),
                    TextVMItem(it.description.take(30), backgroundColor = if (it.isCategorized) null else R.attr.colorSecondary),
                )
            )
        }
    val lowerRecipeGrid =
        transaction.map {
            listOf(
                listOf(
                    TextVMItem("Default"),
                    TextVMItem(it.defaultAmount.toString()),
                ),
                *it.categoryAmounts.map {
                    listOf(
                        TextVMItem(it.key.name),
                        TextVMItem(it.value.toString()),
                    )
                }.toTypedArray()
            )
        }
    val buttons =
        listOf(
            ButtonVMItem(
                title = "Clear",
                onClick = { userClearTransaction() }
            )
        )
}