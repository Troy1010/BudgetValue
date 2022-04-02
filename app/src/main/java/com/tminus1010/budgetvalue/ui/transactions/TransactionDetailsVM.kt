package com.tminus1010.budgetvalue.ui.transactions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.all_layers.KEY1
import com.tminus1010.budgetvalue.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.all_layers.extensions.value
import com.tminus1010.budgetvalue.app.TransactionsInteractor
import com.tminus1010.budgetvalue.data.service.MoshiWithCategoriesProvider
import com.tminus1010.budgetvalue.domain.CategoryAmounts
import com.tminus1010.budgetvalue.domain.Transaction
import com.tminus1010.budgetvalue.framework.android.ShowToast
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.TableViewVMItem
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.TextVMItem
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailsVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val transactionsInteractor: TransactionsInteractor,
    private val showToast: ShowToast,
    private val moshiWithCategoriesProvider: MoshiWithCategoriesProvider,
) : ViewModel() {
    // # User Intents
    fun userClearTransaction() {
        GlobalScope.launch {
            transactionsInteractor.push(transaction.value!!.categorize(CategoryAmounts()))
            navUp.onNext()
        }
    }

    // # Internal
    val transaction =
        savedStateHandle.getLiveData<String>(KEY1).asFlow().map { moshiWithCategoriesProvider.moshi.fromJson<Transaction>(it) }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    // # Events
    val navUp = MutableSharedFlow<Unit>()

    init {
        transaction.filter { it.categoryAmounts.isEmpty() }.observe(viewModelScope) { showToast(NativeText.Simple("This transaction has not been categorized")) }
    }

    // # State
    val transactionInfoTableView =
        transaction.map {
            TableViewVMItem(
                recipeGrid = listOf(
                    listOf(
                        TextVMItem(it.date.toDisplayStr(), backgroundColor = if (it.isCategorized) null else R.attr.colorSecondary),
                        TextVMItem(it.defaultAmount.toString(), backgroundColor = if (it.isCategorized) null else R.attr.colorSecondary),
                        TextVMItem(it.description.take(30), backgroundColor = if (it.isCategorized) null else R.attr.colorSecondary),
                    )
                ),
                shouldFitItemWidthsInsideTable = true,
            )
        }
    val transactionCategoryAmountsTableView =
        transaction.map {
            TableViewVMItem(
                recipeGrid = listOf(
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
                ),
                shouldFitItemWidthsInsideTable = true,
            )
        }
    val buttons =
        flowOf(
            listOf(
                ButtonVMItem(
                    title = "Clear",
                    onClick = ::userClearTransaction
                )
            )
        )
}