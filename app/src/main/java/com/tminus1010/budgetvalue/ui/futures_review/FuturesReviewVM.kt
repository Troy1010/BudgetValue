package com.tminus1010.budgetvalue.ui.futures_review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.data.FuturesRepo
import com.tminus1010.budgetvalue.domain.Future
import com.tminus1010.budgetvalue.domain.TransactionMatcher
import com.tminus1010.budgetvalue.ui.all_features.model.ButtonVMItem
import com.tminus1010.budgetvalue.ui.all_features.model.MenuVMItems
import com.tminus1010.budgetvalue.ui.all_features.model.MenuVMItem
import com.tminus1010.budgetvalue.ui.all_features.model.TextPresentationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class FuturesReviewVM @Inject constructor(
    private val futuresRepo: FuturesRepo,
) : ViewModel() {
    // # User Intents
    fun userDeleteFuture(future: Future) {
        runBlocking { futuresRepo.delete(future) }
    }

    fun userCreateFuture() {
        runBlocking { navToCreateFuture.onNext() }
    }

    // # Events
    val navToFutureDetails = MutableSharedFlow<Future>()
    val navToCreateFuture = MutableSharedFlow<Unit>()

    // # State
    val futuresRecipeGrid =
        futuresRepo.futures
            .map {
                listOf(
                    listOf(
                        TextPresentationModel(TextPresentationModel.Style.HEADER, "Name"),
                        TextPresentationModel(TextPresentationModel.Style.HEADER, "Status"),
                        TextPresentationModel(TextPresentationModel.Style.HEADER, "Search by"),
                    ),
                    *it.map {
                        val menuVMItems =
                            MenuVMItems(
                                MenuVMItem(title = "Delete", onClick = { userDeleteFuture(it) }),
                                MenuVMItem(title = "Edit", onClick = { navToFutureDetails.onNext(it) }),
                            )
                        listOf(
                            TextPresentationModel(TextPresentationModel.Style.TWO, it.name, menuVMItems = menuVMItems),
                            TextPresentationModel(TextPresentationModel.Style.TWO, it.terminationStrategy.displayStr, menuVMItems = menuVMItems),
                            TextPresentationModel(
                                TextPresentationModel.Style.TWO,
                                when (val matcher = it.onImportMatcher) {
                                    is TransactionMatcher.SearchText -> matcher.searchText.take(10)
                                    is TransactionMatcher.ByValue -> matcher.searchTotal.toString()
                                    is TransactionMatcher.Multi -> "Multiple"
                                },
                                menuVMItems = menuVMItems,
                            ),
                        )
                    }.toTypedArray()
                )
            }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val buttons =
        flowOf(
            listOfNotNull(
                ButtonVMItem(title = "Create Future", onClick = { userCreateFuture() }),
            )
        )
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val isNoFutureTextVisible =
        futuresRepo.futures
            .map { it.isEmpty() }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
}