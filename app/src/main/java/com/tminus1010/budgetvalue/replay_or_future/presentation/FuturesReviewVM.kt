package com.tminus1010.budgetvalue.replay_or_future.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.presentation.model.MenuPresentationModel
import com.tminus1010.budgetvalue._core.presentation.model.MenuVMItem
import com.tminus1010.budgetvalue._core.presentation.model.TextPresentationModel
import com.tminus1010.budgetvalue.replay_or_future.data.FuturesRepo
import com.tminus1010.budgetvalue.replay_or_future.domain.BasicFuture
import com.tminus1010.budgetvalue.replay_or_future.domain.IFuture
import com.tminus1010.budgetvalue.replay_or_future.domain.TotalFuture
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FuturesReviewVM @Inject constructor(
    private val futuresRepo: FuturesRepo,
) : ViewModel() {
    // # User Intents
    fun userDeleteFuture(future: IFuture) {
        futuresRepo.delete(future).subscribe()
    }

    // # State
    val recipeGrid =
        futuresRepo.fetchFutures()
            .map {
                listOf(
                    listOf(
                        TextPresentationModel(TextPresentationModel.Style.HEADER, "Name"),
                        TextPresentationModel(TextPresentationModel.Style.HEADER, "Status"),
                        TextPresentationModel(TextPresentationModel.Style.HEADER, "Search by"),
                    ),
                    *it.map {
                        val menuPresentationModel = MenuPresentationModel(MenuVMItem(title = "Delete", onClick = { userDeleteFuture(it) }))
                        listOf(
                            TextPresentationModel(TextPresentationModel.Style.TWO, it.name, menuPresentationModel = menuPresentationModel),
                            TextPresentationModel(TextPresentationModel.Style.TWO, it.terminationStatus.displayStr, menuPresentationModel = menuPresentationModel),
                            TextPresentationModel(
                                TextPresentationModel.Style.TWO,
                                when (it) {
                                    is BasicFuture -> it.searchTexts.first().take(10)
                                    is TotalFuture -> it.searchTotal.toString()
                                    else -> error("Unhandled IFuture:$it")
                                },
                                menuPresentationModel = menuPresentationModel
                            ),
                        )
                    }.toTypedArray()
                )
            }
}