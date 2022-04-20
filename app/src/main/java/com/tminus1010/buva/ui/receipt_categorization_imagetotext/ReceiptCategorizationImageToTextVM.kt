package com.tminus1010.buva.ui.receipt_categorization_imagetotext

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.squareup.moshi.Types
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.KEY2
import com.tminus1010.buva.data.service.MoshiProvider
import com.tminus1010.buva.data.service.MoshiWithCategoriesProvider
import com.tminus1010.buva.domain.Transaction
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ReceiptCategorizationImageToTextVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    moshiWithCategoriesProvider: MoshiWithCategoriesProvider,
    moshiProvider: MoshiProvider,
) : ViewModel() {
    // # View Events

    // # User Intents

    // # Internal
    private val transaction = moshiWithCategoriesProvider.moshi.fromJson<Transaction>(savedStateHandle[KEY1])
        .also { logz("transaction:$it") }
    private val descriptionAndTotal = savedStateHandle.get<String?>(KEY2)?.let { moshiProvider.moshi.adapter<Pair<String, BigDecimal>>(Types.newParameterizedType(Pair::class.java, String::class.java, BigDecimal::class.java)).fromJson(it) }
        .also { logz("descriptionAndTotal:$it") }

    // # Events
    val navUp = MutableSharedFlow<Unit>()

    // # State
    val receiptText = flowOf("item 1    $1.00\nitem 2    $2.00")
    val buttons =
        flowOf(
            listOf(
                ButtonVMItem(
                    title = "Show categorization so far",
                    onClick = { logz("rewq") }
                ),
                ButtonVMItem(
                    title = "Submit Categorization",
                    onClick = { logz("asdf") },
                ),
            )
        )
}
