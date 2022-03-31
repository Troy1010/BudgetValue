package com.tminus1010.budgetvalue.ui.receipt_categorization

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.all_layers.extensions.easyEmit
import com.tminus1010.budgetvalue.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.domain.Transaction
import com.tminus1010.budgetvalue.ui.all_features.SubFragEventSharedVM
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.budgetvalue.ui.choose_amount.ChooseAmountSubFrag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ReceiptCategorizationHostVM @Inject constructor(
    private val subFragEventSharedVM: SubFragEventSharedVM,
    private val receiptCategorizationSharedVM: ReceiptCategorizationSharedVM,
) : ViewModel() {
    // # View Events
    val transaction = MutableStateFlow<Transaction?>(null)
    val currentFrag = MutableStateFlow<Fragment?>(null)

    // # User Intents
    fun userShowCategorizationSoFar() {
        subFragEventSharedVM.showFragment.easyEmit(ReceiptCategorizationSoFarSubFrag())
    }

    fun userSubmitCategorization() {
        receiptCategorizationSharedVM.submitCategorization()
        navUp.easyEmit(Unit)
    }

    // # Presentation Events
    val navUp = MutableSharedFlow<Unit>()

    // # State
    val fragment = subFragEventSharedVM.showFragment.onStart { emit(ChooseAmountSubFrag()) }
    val amountLeft = receiptCategorizationSharedVM.amountLeftToCategorize.map { it.toString().toMoneyBigDecimal().toString() }
    val description = transaction.map { it!!.description }
    val buttons =
        flowOf(
            listOf(
                ButtonVMItem(
                    isEnabled2 = currentFrag.map { it !is ReceiptCategorizationSoFarSubFrag },
                    title = "Show categorization so far",
                    onClick = { userShowCategorizationSoFar() }
                ),
                ButtonVMItem(
                    title = "Submit Categorization",
                    onClick = { userSubmitCategorization() }
                ),
            )
        )
}
