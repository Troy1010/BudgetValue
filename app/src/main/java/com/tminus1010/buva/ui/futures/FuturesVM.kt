package com.tminus1010.buva.ui.futures

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.data.FuturesRepo
import com.tminus1010.buva.domain.Future
import com.tminus1010.buva.domain.TransactionMatcher
import com.tminus1010.buva.ui.all_features.Navigator
import com.tminus1010.buva.ui.all_features.view_model_item.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class FuturesVM @Inject constructor(
    private val futuresRepo: FuturesRepo,
    private val navigator: Navigator,
) : ViewModel() {
    // # User Intents
    fun userDeleteFuture(future: Future) {
        runBlocking { futuresRepo.delete(future) }
    }

    // # Events
    val navToFutureDetails = MutableSharedFlow<Future>()

    // # State
    val futuresTableView =
        futuresRepo.futures
            .map {
                TableViewVMItem(
                    recipeGrid = listOf(
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
                                    when (val matcher = it.onImportTransactionMatcher) {
                                        is TransactionMatcher.SearchText -> matcher.searchText.take(10)
                                        is TransactionMatcher.ByValue -> matcher.searchTotal.toString()
                                        is TransactionMatcher.Multi -> "Multiple"
                                        null -> "None"
                                    },
                                    menuVMItems = menuVMItems,
                                ),
                            )
                        }.toTypedArray()
                    ),
                    shouldFitItemWidthsInsideTable = true,
                    rowFreezeCount = 1,
                )
            }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val buttons =
        flowOf(
            listOfNotNull(
                ButtonVMItem(
                    title = "Create Future",
                    onClick = navigator::navToCreateFuture
                ),
            )
        )
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val isNoFutureTextVisible =
        futuresRepo.futures
            .map { it.isEmpty() }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
}