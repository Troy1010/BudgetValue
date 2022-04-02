package com.tminus1010.budgetvalue.ui.transactions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.all_layers.KEY1
import com.tminus1010.budgetvalue.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.app.TransactionsInteractor
import com.tminus1010.budgetvalue.data.service.MoshiWithCategoriesProvider
import com.tminus1010.budgetvalue.domain.CategoryAmounts
import com.tminus1010.budgetvalue.domain.Transaction
import com.tminus1010.budgetvalue.framework.android.ShowToast
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.TableViewVMItem
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.TextVMItem
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailsVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val transactionsInteractor: TransactionsInteractor,
    showToast: ShowToast,
    moshiWithCategoriesProvider: MoshiWithCategoriesProvider,
) : ViewModel() {
    // # User Intents
    fun userClearTransaction() {
        GlobalScope.launch {
            transactionsInteractor.push(transaction.categorize(CategoryAmounts()))
            navUp.onNext()
        }
    }

    // # Internal
    private val transaction = moshiWithCategoriesProvider.moshi.fromJson<Transaction>(savedStateHandle.get(KEY1))!!

    // # Events
    val navUp = MutableSharedFlow<Unit>()

    init {
        if (transaction.categoryAmounts.isEmpty()) showToast(NativeText.Simple("This transaction has not been categorized"))
    }

    // # State
    val transactionInfoTableView =
        flowOf(
            TableViewVMItem(
                recipeGrid = listOf(
                    listOf(
                        TextVMItem(transaction.date.toDisplayStr(), backgroundColor = if (transaction.isCategorized) null else R.attr.colorSecondary),
                        TextVMItem(transaction.defaultAmount.toString(), backgroundColor = if (transaction.isCategorized) null else R.attr.colorSecondary),
                        TextVMItem(transaction.description.take(30), backgroundColor = if (transaction.isCategorized) null else R.attr.colorSecondary),
                    )
                ),
                shouldFitItemWidthsInsideTable = true,
            )
        )
    val transactionCategoryAmountsTableView =
        flowOf(
            TableViewVMItem(
                recipeGrid = listOf(
                    listOf(
                        TextVMItem("Default"),
                        TextVMItem(transaction.defaultAmount.toString()),
                    ),
                    *transaction.categoryAmounts.map {
                        listOf(
                            TextVMItem(it.key.name),
                            TextVMItem(it.value.toString()),
                        )
                    }.toTypedArray()
                ),
                shouldFitItemWidthsInsideTable = true,
            )
        )
    val buttons =
        flowOf(
            listOf(
                ButtonVMItem(
                    title = "Clear",
                    onClick = ::userClearTransaction,
                )
            )
        )
}