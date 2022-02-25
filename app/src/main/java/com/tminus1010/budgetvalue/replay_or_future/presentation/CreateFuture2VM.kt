package com.tminus1010.budgetvalue.replay_or_future.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.all.extensions.onNext
import com.tminus1010.budgetvalue._core.all.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.presentation.model.*
import com.tminus1010.budgetvalue.transactions.presentation.model.SearchType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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

    fun userSetSearchType(searchType: SearchType) {
        this.searchType.onNext(searchType)
    }

    fun userSetDescription(s: String) {
        description.onNext(s)
    }

    // # Internal
    val totalGuess = MutableStateFlow(BigDecimal.TEN)
    val isPermanent = MutableStateFlow(false)
    val searchType = MutableStateFlow(SearchType.DESCRIPTION)
    val description = MutableStateFlow<String?>(null)

    // # Events
    val navUp = MutableSharedFlow<Unit>()

    // # State
    val otherInput =
        searchType.map { searchType ->
            listOfNotNull(
                listOf(
                    TextPresentationModel(text1 = "Total Guess"),
                    MoneyEditVMItem(text1 = totalGuess.value.toString(), onDone = { userSetTotalGuess(it) }),
                ),
                listOf(
                    TextPresentationModel(text1 = "Search Type"),
                    SpinnerVMItem(SearchType.values(), searchType, onNewItem = { userSetSearchType(it) }),
                ),
                if (listOf(SearchType.DESCRIPTION_AND_TOTAL, SearchType.DESCRIPTION).any { it == searchType })
                    listOf(
                        TextPresentationModel(text1 = "Description"),
                        EditTextVMItem(text = description.value, onDone = { userSetDescription(it) }),
                    )
                else null,
                listOf(
                    TextPresentationModel(text1 = "Is Permanent"),
                    CheckboxVMItem(isPermanent.value, onCheckChanged = { userSetIsPermanent(it) }),
                ),
            )
        }
    val recipeGrid =
        flowOf(
            listOf(
                listOf(
                    TextPresentationModel(TextPresentationModel.Style.HEADER, "Category"),
                    TextPresentationModel(TextPresentationModel.Style.HEADER, "Amount"),
                    TextPresentationModel(TextPresentationModel.Style.HEADER, "Fill"),
                ),
            )
        )
    val buttons =
        flowOf(
            listOfNotNull(
                ButtonVMItem(
                    title = "Submit",
                    onClick = { userSubmit() },
                ),
            )
        )
}