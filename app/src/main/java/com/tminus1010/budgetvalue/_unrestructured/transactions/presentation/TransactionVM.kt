package com.tminus1010.budgetvalue._unrestructured.transactions.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.domain.Transaction
import com.tminus1010.budgetvalue.app.TransactionsInteractor
import com.tminus1010.budgetvalue.domain.CategoryAmounts
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.TextVMItem
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionVM @Inject constructor(
    private val transactionsInteractor: TransactionsInteractor,
) : ViewModel() {
    // # Setup
    val transaction = BehaviorSubject.create<Transaction>()

    // # User Intents
    fun userClearTransaction() {
        GlobalScope.launch {
            transactionsInteractor.saveTransactions(transaction.value!!.categorize(CategoryAmounts()))
            navUp.onNext(Unit)
        }
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