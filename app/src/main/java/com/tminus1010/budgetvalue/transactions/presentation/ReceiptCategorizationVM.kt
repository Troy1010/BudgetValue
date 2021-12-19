package com.tminus1010.budgetvalue.transactions.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue._core.all.extensions.easyEmit
import com.tminus1010.budgetvalue._core.all.extensions.easyStateIn
import com.tminus1010.budgetvalue._core.data.MoshiProvider.moshi
import com.tminus1010.budgetvalue._core.data.MoshiWithCategoriesProvider
import com.tminus1010.budgetvalue._core.domain.CategoryAmounts
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.app.CurrentChosenAmountProvider
import com.tminus1010.budgetvalue.transactions.app.Transaction
import com.tminus1010.budgetvalue.transactions.app.interactor.SaveTransactionInteractor
import com.tminus1010.budgetvalue.transactions.view.ChooseAmountFrag
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ReceiptCategorizationVM @Inject constructor(
    moshiWithCategoriesProvider: MoshiWithCategoriesProvider,
    private val saveTransactionInteractor: SaveTransactionInteractor,
    private val currentChosenAmountProvider: CurrentChosenAmountProvider
) : ViewModel() {
    // # Setup
    val transaction = MutableStateFlow<Transaction?>(null)

    // # User Intents
    val userSetAmount = MutableSharedFlow<String?>()
    val userSelectCategory = MutableSharedFlow<String?>()
    val userFill = MutableSharedFlow<Unit>()
    val userShowNewInnerFragment = MutableStateFlow(ChooseAmountFrag())
    fun userSubmitPartialCategorization() {
        categoryAmounts[currentCategory.value!!] = categoryAmounts[currentCategory.value!!]?.let { it + currentAmount.value!! } ?: currentAmount.value!!
        userSelectCategory.easyEmit(null)
        userSetAmount.easyEmit(null)
    }

    fun userSubmitCategorization() {
        saveTransactionInteractor.saveTransaction(transaction.value!!.copy(categoryAmounts = categoryAmounts))
        navUp.easyEmit(Unit)
        // # Assumes VM will be cleared.
    }

    // # Internal
    private val currentCategory =
        userSelectCategory.map { moshiWithCategoriesProvider.moshi.fromJson<Category>(it) }
            .easyStateIn(viewModelScope, null)
    private val currentAmount =
        merge(
            userSetAmount.map { moshi.fromJson<BigDecimal>(it) },
            userFill.map { CategoryAmounts(categoryAmounts).defaultAmount(transaction.value!!.amount) }
        )
            .easyStateIn(viewModelScope, null)
    private val categoryAmounts = mutableMapOf<Category, BigDecimal>()

    // # Presentation Events
    val navUp = MutableSharedFlow<Unit>()

    // # Presentation State
    val fragment = userShowNewInnerFragment
    val description = transaction.map { it!!.description }
    val currentCategorizationAmount = currentChosenAmountProvider.currentChosenAmount.map { it.toString() }
    val buttons =
        MutableStateFlow(
            listOf(
                ButtonVMItem(
                    title = "Submit Partial Categorization",
                    onClick = { userSubmitPartialCategorization() }
                ),
                ButtonVMItem(
                    title = "Submit Categorization",
                    onClick = { userSubmitCategorization() }
                )
            )
        )
}
