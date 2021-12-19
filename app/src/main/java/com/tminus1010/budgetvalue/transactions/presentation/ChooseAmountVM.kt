package com.tminus1010.budgetvalue.transactions.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue._core.all.extensions.easyEmit
import com.tminus1010.budgetvalue._core.all.extensions.observe
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue.transactions.app.CurrentChosenAmountProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ChooseAmountVM @Inject constructor(
    currentChosenAmountProvider: CurrentChosenAmountProvider,
) : ViewModel() {
    // # User Intents
    val userPlus10 = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { currentChosenAmountProvider.currentChosenAmount.value = currentChosenAmountProvider.currentChosenAmount.value + BigDecimal("10") } }
    val userPlus1 = MutableSharedFlow<Unit>()
    val userPlus01 = MutableSharedFlow<Unit>()
    val userPlus001 = MutableSharedFlow<Unit>()
    val userMinus10 = MutableSharedFlow<Unit>()
    val userMinus1 = MutableSharedFlow<Unit>()
    val userMinus01 = MutableSharedFlow<Unit>()
    val userMinus001 = MutableSharedFlow<Unit>()
    val userSetAmount = MutableSharedFlow<String>()
    val userSubmitAmount = MutableSharedFlow<Unit>()

    // # Presentation State
    val buttons =
        listOf(
            listOf(
                ButtonVMItem(
                    title = "Plus $10",
                    onClick = { userPlus10.easyEmit(Unit) }
                ),
                ButtonVMItem(
                    title = "Minus $10",
                    onClick = { userMinus10.easyEmit(Unit) }
                ),
            ),
            listOf(
                ButtonVMItem(
                    title = "Plus $1",
                    onClick = {

                    }
                ),
                ButtonVMItem(
                    title = "Minus $1",
                    onClick = {

                    }
                ),
            ),
            listOf(
                ButtonVMItem(
                    title = "Plus $0.10",
                    onClick = {

                    }
                ),
                ButtonVMItem(
                    title = "Minus $0.10",
                    onClick = {

                    }
                ),
            ),
            listOf(
                ButtonVMItem(
                    title = "Plus $0.01",
                    onClick = {

                    }
                ),
                ButtonVMItem(
                    title = "Minus $0.01",
                    onClick = {

                    }
                ),
            ),
        )
}
