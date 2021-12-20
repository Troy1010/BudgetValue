package com.tminus1010.budgetvalue.transactions.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue._core.all.extensions.easyEmit
import com.tminus1010.budgetvalue._core.all.extensions.observe
import com.tminus1010.budgetvalue._core.all.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue.transactions.app.ReceiptCategorizationInteractor
import com.tminus1010.budgetvalue.transactions.app.SubFragEventProvider
import com.tminus1010.budgetvalue.transactions.view.ChooseCategorySubFrag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ChooseAmountVM @Inject constructor(
    receiptCategorizationInteractor: ReceiptCategorizationInteractor,
    subFragEventProvider: SubFragEventProvider
) : ViewModel() {
    // # User Intents
    val userPlus100 = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { receiptCategorizationInteractor.currentChosenAmount.value = receiptCategorizationInteractor.currentChosenAmount.value + BigDecimal("100") } }
    val userPlus10 = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { receiptCategorizationInteractor.currentChosenAmount.value = receiptCategorizationInteractor.currentChosenAmount.value + BigDecimal("10") } }
    val userPlus1 = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { receiptCategorizationInteractor.currentChosenAmount.value = receiptCategorizationInteractor.currentChosenAmount.value + BigDecimal("1") } }
    val userPlus01 = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { receiptCategorizationInteractor.currentChosenAmount.value = receiptCategorizationInteractor.currentChosenAmount.value + BigDecimal("0.1") } }
    val userPlus001 = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { receiptCategorizationInteractor.currentChosenAmount.value = receiptCategorizationInteractor.currentChosenAmount.value + BigDecimal("0.01") } }
    val userMinus100 = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { receiptCategorizationInteractor.currentChosenAmount.value = receiptCategorizationInteractor.currentChosenAmount.value - BigDecimal("100") } }
    val userMinus10 = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { receiptCategorizationInteractor.currentChosenAmount.value = receiptCategorizationInteractor.currentChosenAmount.value - BigDecimal("10") } }
    val userMinus1 = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { receiptCategorizationInteractor.currentChosenAmount.value = receiptCategorizationInteractor.currentChosenAmount.value - BigDecimal("1") } }
    val userMinus01 = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { receiptCategorizationInteractor.currentChosenAmount.value = receiptCategorizationInteractor.currentChosenAmount.value - BigDecimal("0.1") } }
    val userMinus001 = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { receiptCategorizationInteractor.currentChosenAmount.value = receiptCategorizationInteractor.currentChosenAmount.value - BigDecimal("0.01") } }
    val userSetAmount = MutableSharedFlow<String>()
        .apply { observe(viewModelScope) { receiptCategorizationInteractor.currentChosenAmount.value = it.toMoneyBigDecimal() } }
    val userShowChooseCategory = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { subFragEventProvider.showFragment.easyEmit(ChooseCategorySubFrag()) } }

    // # Presentation State
    val amount = receiptCategorizationInteractor.currentChosenAmount.map { it.toString().toMoneyBigDecimal().toString() }
    val buttons =
        listOf(
            listOf(
                ButtonVMItem(
                    title = "Plus $100",
                    onClick = { userPlus100.easyEmit(Unit) }
                ),
                ButtonVMItem(
                    title = "Minus $100",
                    onClick = { userMinus100.easyEmit(Unit) }
                ),
            ),
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
                    onClick = { userPlus1.easyEmit(Unit) }
                ),
                ButtonVMItem(
                    title = "Minus $1",
                    onClick = { userMinus1.easyEmit(Unit) }
                ),
            ),
            listOf(
                ButtonVMItem(
                    title = "Plus $0.10",
                    onClick = { userPlus01.easyEmit(Unit) }
                ),
                ButtonVMItem(
                    title = "Minus $0.10",
                    onClick = { userMinus01.easyEmit(Unit) }
                ),
            ),
            listOf(
                ButtonVMItem(
                    title = "Plus $0.01",
                    onClick = { userPlus001.easyEmit(Unit) }
                ),
                ButtonVMItem(
                    title = "Minus $0.01",
                    onClick = { userMinus001.easyEmit(Unit) }
                ),
            ),
        )
}
