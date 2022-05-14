package com.tminus1010.buva.ui.receipt_categorization

import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.squareup.moshi.Types
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.KEY2
import com.tminus1010.buva.all_layers.extensions.easyEmit
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.buva.data.service.MoshiProvider
import com.tminus1010.buva.data.service.MoshiWithCategoriesProvider
import com.tminus1010.buva.domain.Transaction
import com.tminus1010.buva.ui.all_features.SubFragEventSharedVM
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.buva.ui.choose_amount.ChooseAmountSubFrag
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ReceiptCategorizationHostVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    moshiProvider: MoshiProvider,
    private val subFragEventSharedVM: SubFragEventSharedVM,
    private val receiptCategorizationSharedVM: ReceiptCategorizationSharedVM,
) : ViewModel() {
    // # View Events
    val currentFrag = MutableStateFlow<Fragment?>(null)

    // # User Intents
    fun userShowCategorizationSoFar() {
        subFragEventSharedVM.showFragment.easyEmit(ReceiptCategorizationSoFarSubFrag())
    }

    fun userSubmitCategorization() {
        transaction.value?.also { receiptCategorizationSharedVM.submitCategorization(it) }
        if (descriptionAndTotal != null) receiptCategorizationSharedVM.userSubmitCategorization()
        navUp.easyEmit(Unit)
    }

    // # Internal
    private val transaction = savedStateHandle.getLiveData<Transaction>(KEY1)
        .also { it.value?.also { receiptCategorizationSharedVM.total.onNext(it.amount) } }
    private val descriptionAndTotal = savedStateHandle.get<String?>(KEY2)?.let { moshiProvider.moshi.adapter<Pair<String, BigDecimal>>(Types.newParameterizedType(Pair::class.java, String::class.java, BigDecimal::class.java)).fromJson(it) }
        ?.also { receiptCategorizationSharedVM.total.onNext(it.second) }

    // # Events
    val navUp = MutableSharedFlow<Unit>()

    // # State
    val fragment = subFragEventSharedVM.showFragment.onStart { emit(ChooseAmountSubFrag()) }
    val amountLeft = receiptCategorizationSharedVM.amountLeftToCategorize.map { it.toString().toMoneyBigDecimal().toString() }
    val description = flowOf(transaction.value?.description ?: descriptionAndTotal?.first)
    val buttons =
        flowOf(
            listOf(
                ButtonVMItem(
                    isEnabled2 = currentFrag.map { it !is ReceiptCategorizationSoFarSubFrag },
                    title = "Show categorization so far",
                    onClick = ::userShowCategorizationSoFar
                ),
                ButtonVMItem(
                    title = "Submit Categorization",
                    onClick = ::userSubmitCategorization
                ),
            )
        )
}
