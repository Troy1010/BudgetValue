package com.tminus1010.buva.ui.transactions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.app.TransactionsInteractor
import com.tminus1010.buva.environment.MoshiWithCategoriesProvider
import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.buva.domain.Transaction
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.TableViewVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.TextVMItem
import com.tminus1010.tmcommonkotlin.androidx.ShowToast
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
    private val savedStateHandle: SavedStateHandle,
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

    // # Private
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