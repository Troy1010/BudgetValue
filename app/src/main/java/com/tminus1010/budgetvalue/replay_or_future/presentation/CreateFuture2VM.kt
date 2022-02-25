package com.tminus1010.budgetvalue.replay_or_future.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.all.extensions.onNext
import com.tminus1010.budgetvalue._core.all.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue._core.presentation.model.CheckboxVMItem
import com.tminus1010.budgetvalue._core.presentation.model.MoneyEditVMItem
import com.tminus1010.budgetvalue._core.presentation.model.TextPresentationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CreateFuture2VM @Inject constructor(
) : ViewModel() {
    // # User Intents
    fun userSubmit() {
        TODO()
        navUp.onNext()
    }

    fun userSetTotalGuess(s: String) {
        totalGuess.onNext(s.toMoneyBigDecimal())
    }

    fun userSetIsPermanent(b: Boolean) {
        isPermanent.onNext(b)
    }

    // # Internal
    val totalGuess = MutableStateFlow(BigDecimal.TEN)
    val isPermanent = MutableStateFlow(false)

    // # Events
    val navUp = MutableSharedFlow<Unit>()

    // # State
    val otherInput =
        flowOf(
            listOfNotNull(
                listOf(
                    TextPresentationModel(text1 = "Total Guess"),
                    MoneyEditVMItem(text1 = totalGuess.value.toString(), onDone = { userSetTotalGuess(it) }),
                ),
//                listOf(
//                    itemTextViewRB().create(createFutureVM.searchTypeHeader),
//                    itemSpinnerRF().create(SearchType.values(), createFutureVM.searchType.value, createFutureVM::userSetSearchType),
//                ),
//                if (listOf(SearchType.DESCRIPTION_AND_TOTAL, SearchType.DESCRIPTION).any { it == searchType })
//                    listOf(
//                        itemTextViewRB().create(createFutureVM.searchDescriptionHeader),
//                        itemEditTextRF().create(createFutureVM.searchDescription, createFutureVM::userSetSearchDescription, createFutureVM.searchDescriptionMenuVMItems),
//                    )
//                else null,
                listOf(
                    TextPresentationModel(text1 = "Is Permanent"),
                    CheckboxVMItem(isPermanent.value, onCheckChanged = { userSetIsPermanent(it) }),
                ),
            )
        )
    val recipeGrid =
        flowOf(
            listOf(
                listOf(
                    TextPresentationModel(TextPresentationModel.Style.HEADER, "Category"),
                    TextPresentationModel(TextPresentationModel.Style.HEADER, "Amount"),
                    TextPresentationModel(TextPresentationModel.Style.HEADER, "Fill"),
                )
            )
        )
    val buttons =
        flowOf(
            listOfNotNull(
                ButtonVMItem(
                    title = "Submit",
                    onClick = { userSubmit() }
                ),
            )
        )
}