package com.tminus1010.buva.ui.all_features

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.tminus1010.buva.all_layers.extensions.remove
import com.tminus1010.buva.all_layers.extensions.replaceFirst
import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.buva.domain.TransactionMatcher
import com.tminus1010.buva.domain.flattened
import com.tminus1010.buva.ui.all_features.view_model_item.EditTextVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.MenuVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.MenuVMItems
import com.tminus1010.buva.ui.all_features.view_model_item.TextVMItem
import com.tminus1010.tmcommonkotlin.customviews.IHasToViewItemRecipe
import javax.inject.Inject

class TransactionMatcherPresentationFactory @Inject constructor() {
    fun viewModelItems(transactionMatcher: LiveData<TransactionMatcher?>, onChange: (TransactionMatcher) -> Unit, userNavToChooseTransactionForTransactionMatcher: (TransactionMatcher) -> Unit): LiveData<List<List<IHasToViewItemRecipe>>> {
        fun userUpdateSearchTotal(s: String) {
            onChange(TransactionMatcher.Multi(transactionMatcher.value!!.flattened().replaceFirst({ it == transactionMatcher }, TransactionMatcher.ByValue(s.toMoneyBigDecimal()))))
        }

        fun userUpdateSearchText(s: String) {
            onChange(TransactionMatcher.Multi(transactionMatcher.value!!.flattened().replaceFirst({ it == transactionMatcher }, TransactionMatcher.SearchText(s))))
        }

        fun userRemoveTransactionMatcher(transactionMatcherToRemove: TransactionMatcher) {
            onChange(TransactionMatcher.Multi(transactionMatcher.value!!.flattened().remove { it == transactionMatcherToRemove }))
        }
        return transactionMatcher.map {
            it.flattened().map { transactionMatcher ->
                when (transactionMatcher) {
                    is TransactionMatcher.ByValue ->
                        listOf(
                            TextVMItem("Search Total"),
                            EditTextVMItem(
                                text = transactionMatcher.searchTotal.toString(),
                                onDone = ::userUpdateSearchTotal,
                                menuVMItems = MenuVMItems(
                                    MenuVMItem(
                                        title = "Delete",
                                        onClick = { userRemoveTransactionMatcher(transactionMatcher) }
                                    ),
                                    MenuVMItem(
                                        title = "Copy from Transactions",
                                        onClick = { userNavToChooseTransactionForTransactionMatcher(transactionMatcher) }
                                    ),
                                )
                            )
                        )
                    is TransactionMatcher.SearchText ->
                        listOf(
                            TextVMItem("Search Text"),
                            EditTextVMItem(
                                text = transactionMatcher.searchText,
                                onDone = ::userUpdateSearchText,
                                menuVMItems = MenuVMItems(
                                    MenuVMItem(
                                        title = "Delete",
                                        onClick = { userRemoveTransactionMatcher(transactionMatcher) }
                                    ),
                                    MenuVMItem(
                                        title = "Copy from Transactions",
                                        onClick = { userNavToChooseTransactionForTransactionMatcher(transactionMatcher) }
                                    ),
                                )
                            )
                        )
                    else -> error("Unhandled type")
                }
            }
        }
    }
}