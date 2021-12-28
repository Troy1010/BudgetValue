package com.tminus1010.budgetvalue.transactions.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.all.extensions.easyEmit
import com.tminus1010.budgetvalue._core.all.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue.transactions.app.ReceiptCategorizationInteractor
import com.tminus1010.budgetvalue.transactions.app.SubFragEventProvider
import com.tminus1010.budgetvalue.transactions.app.Transaction
import com.tminus1010.budgetvalue.transactions.view.ChooseAmountSubFrag
import com.tminus1010.budgetvalue.transactions.view.ReceiptCategorizationSoFarSubFrag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class ReceiptCategorizationVM @Inject constructor(
    private val subFragEventProvider: SubFragEventProvider,
    receiptCategorizationInteractor: ReceiptCategorizationInteractor
) : ViewModel() {
    // # Setup
    val transaction = MutableStateFlow<Transaction?>(null)

    // # User Intents
    fun userShowCategorizationSoFar() {
        subFragEventProvider.showFragment.easyEmit(ReceiptCategorizationSoFarSubFrag())
    }

    // # Presentation Events
    val navUp = MutableSharedFlow<Unit>()

    // # Presentation State
    val fragment = subFragEventProvider.showFragment.onStart { emit(ChooseAmountSubFrag()) }
    val amountLeft = receiptCategorizationInteractor.amountLeftToCategorize.map { it.toString().toMoneyBigDecimal().toString() }
    val description = transaction.map { it!!.description }
    val buttons =
        MutableStateFlow(
            listOf(
                ButtonVMItem(
                    title = "Show categorization so far",
                    onClick = { userShowCategorizationSoFar() }
                ),
                ButtonVMItem(
                    title = "Submit Categorization",
                    onClick = { receiptCategorizationInteractor.userSubmitCategorization() }
                ),
            )
        )
}
