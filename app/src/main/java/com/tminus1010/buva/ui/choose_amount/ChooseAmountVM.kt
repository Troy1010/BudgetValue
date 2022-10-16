package com.tminus1010.buva.ui.choose_amount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.buva.all_layers.extensions.easyEmit
import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.buva.app.TransactionsInteractor
import com.tminus1010.buva.ui.all_features.SubFragEventSharedVM
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.MenuVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.MenuVMItems
import com.tminus1010.buva.ui.all_features.view_model_item.TableViewVMItem
import com.tminus1010.buva.ui.choose_category.ChooseCategorySubFrag
import com.tminus1010.buva.ui.receipt_categorization.ReceiptCategorizationSharedVM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ChooseAmountVM @Inject constructor(
    receiptCategorizationSharedVM: ReceiptCategorizationSharedVM,
    subFragEventSharedVM: SubFragEventSharedVM,
    private val transactionsInteractor: TransactionsInteractor,
) : ViewModel() {
    // # User Intents
    val userPlus100 = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { receiptCategorizationSharedVM.rememberedAmount.value = receiptCategorizationSharedVM.rememberedAmount.value + BigDecimal("100") } }
    val userPlus10 = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { receiptCategorizationSharedVM.rememberedAmount.value = receiptCategorizationSharedVM.rememberedAmount.value + BigDecimal("10") } }
    val userPlus1 = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { receiptCategorizationSharedVM.rememberedAmount.value = receiptCategorizationSharedVM.rememberedAmount.value + BigDecimal("1") } }
    val userPlus01 = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { receiptCategorizationSharedVM.rememberedAmount.value = receiptCategorizationSharedVM.rememberedAmount.value + BigDecimal("0.1") } }
    val userPlus001 = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { receiptCategorizationSharedVM.rememberedAmount.value = receiptCategorizationSharedVM.rememberedAmount.value + BigDecimal("0.01") } }
    val userMinus100 = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { receiptCategorizationSharedVM.rememberedAmount.value = receiptCategorizationSharedVM.rememberedAmount.value - BigDecimal("100") } }
    val userMinus10 = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { receiptCategorizationSharedVM.rememberedAmount.value = receiptCategorizationSharedVM.rememberedAmount.value - BigDecimal("10") } }
    val userMinus1 = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { receiptCategorizationSharedVM.rememberedAmount.value = receiptCategorizationSharedVM.rememberedAmount.value - BigDecimal("1") } }
    val userMinus01 = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { receiptCategorizationSharedVM.rememberedAmount.value = receiptCategorizationSharedVM.rememberedAmount.value - BigDecimal("0.1") } }
    val userMinus001 = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { receiptCategorizationSharedVM.rememberedAmount.value = receiptCategorizationSharedVM.rememberedAmount.value - BigDecimal("0.01") } }
    val userFillAmount = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { receiptCategorizationSharedVM.fill() } }
    val userSetAmount = MutableSharedFlow<String>()
        .apply { observe(viewModelScope) { receiptCategorizationSharedVM.rememberedAmount.value = it.toMoneyBigDecimal() } }
    val userSubmitAmount = MutableSharedFlow<Unit>()
        .apply { observe(viewModelScope) { subFragEventSharedVM.showFragment.easyEmit(ChooseCategorySubFrag()) } }

    // # State
    val amount = receiptCategorizationSharedVM.rememberedAmount.map { it.toString().toMoneyBigDecimal().toString() }
    val amountMenuVMItem =
        MenuVMItems(
            MenuVMItem(
                title = "Fill",
                onClick = { userFillAmount.easyEmit(Unit) },
            ),
        )
    val plusMinusButtonsTableView =
        flowOf(
            TableViewVMItem(
                recipeGrid = listOf(
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
                ),
                shouldFitItemWidthsInsideTable = true,
            )
        )
}
