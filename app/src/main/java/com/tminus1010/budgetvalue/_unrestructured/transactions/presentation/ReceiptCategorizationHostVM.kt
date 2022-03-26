package com.tminus1010.budgetvalue._unrestructured.transactions.presentation

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.all_layers.extensions.easyEmit
import com.tminus1010.budgetvalue.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.ui.all_features.model.ButtonVMItem
import com.tminus1010.budgetvalue._unrestructured.transactions.app.ReceiptCategorizationInteractor
import com.tminus1010.budgetvalue.ui.all_features.SubFragEventProvider
import com.tminus1010.budgetvalue._unrestructured.transactions.app.Transaction
import com.tminus1010.budgetvalue._unrestructured.transactions.view.ChooseAmountSubFrag
import com.tminus1010.budgetvalue._unrestructured.transactions.view.ReceiptCategorizationSoFarSubFrag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class ReceiptCategorizationHostVM @Inject constructor(
    private val subFragEventProvider: SubFragEventProvider,
    private val receiptCategorizationInteractor: ReceiptCategorizationInteractor
) : ViewModel() {
    // # View Events
    val transaction = MutableStateFlow<Transaction?>(null)
    val currentFrag = MutableStateFlow<Fragment?>(null)

    // # User Intents
    fun userShowCategorizationSoFar() {
        subFragEventProvider.showFragment.easyEmit(ReceiptCategorizationSoFarSubFrag())
    }

    fun userSubmitCategorization() {
        receiptCategorizationInteractor.submitCategorization()
        navUp.easyEmit(Unit)
    }

    // # Presentation Events
    val navUp = MutableSharedFlow<Unit>()

    // # State
    val fragment = subFragEventProvider.showFragment.onStart { emit(ChooseAmountSubFrag()) }
    val amountLeft = receiptCategorizationInteractor.amountLeftToCategorize.map { it.toString().toMoneyBigDecimal().toString() }
    val description = transaction.map { it!!.description }
    val buttons =
        MutableStateFlow(
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