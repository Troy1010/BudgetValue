package com.tminus1010.budgetvalue.replay_or_future.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.presentation.model.TextPresentationModel
import com.tminus1010.budgetvalue._core.presentation.model.TextVMItem
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

@HiltViewModel
class CreateFuture2VM @Inject constructor(
) : ViewModel() {
    val recipeGrid =
        Observable.just(
            listOf(
                listOf(
                    TextPresentationModel(TextPresentationModel.Style.HEADER, "Category"),
                    TextPresentationModel(TextPresentationModel.Style.HEADER, "Amount"),
                    TextPresentationModel(TextPresentationModel.Style.HEADER, "Fill"),
                )
            )
        )
}