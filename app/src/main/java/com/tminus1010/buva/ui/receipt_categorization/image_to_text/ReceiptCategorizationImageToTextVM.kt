package com.tminus1010.buva.ui.receipt_categorization.image_to_text

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.view.View
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Types
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.KEY2
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.environment.MoshiProvider
import com.tminus1010.buva.environment.MoshiWithCategoriesProvider
import com.tminus1010.buva.domain.Transaction
import com.tminus1010.buva.ui.all_features.ThrobberSharedVM
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.tmcommonkotlin.androidx.ShowAlertDialog
import com.tminus1010.tmcommonkotlin.androidx.ShowToast
import com.tminus1010.tmcommonkotlin.androidx.extensions.waitForBitmapAndSetUpright
import com.tminus1010.tmcommonkotlin.coroutines.extensions.use
import com.tminus1010.tmcommonkotlin.imagetotext.ImageToText
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.io.File
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ReceiptCategorizationImageToTextVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    moshiWithCategoriesProvider: MoshiWithCategoriesProvider,
    moshiProvider: MoshiProvider,
    private val imageToText: ImageToText,
    private val throbberSharedVM: ThrobberSharedVM,
    private val showToast: ShowToast,
) : ViewModel() {
    // # View Events
    val showAlertDialog = MutableSharedFlow<ShowAlertDialog>(1)
    fun newImage(file: File) {
        viewModelScope.launch { readoutText.emit(createSpannableStringAndFormatForReadout(imageToText(file.waitForBitmapAndSetUpright()))) }.use(throbberSharedVM)
    }

    fun newReceiptText(s: CharSequence) {
        receiptText.onNext(s)
    }

    // # User Intents
    fun userAddLine(s: String) {
        receiptText.onNext(createSpannableStringAndFormatForReceipt(receiptText.value?.let { "${it}\n$s" } ?: ""))
    }

    // # Private
    private fun createSpannableStringAndFormatForReadout(s: String?): SpannableString {
        return s
            ?.let { Regex("""[0-9]+\s*\.\s*[0-9]\s*[0-9]""").replace(it) { it.value.filter { !it.isWhitespace() } } }
            ?.replace("\n\n", "\n")
            .let { SpannableString(it) }
            .also { spannableString ->
                Regex("""(.*)($|\n)""").findAll(spannableString).forEach {
                    spannableString.setSpan(
                        object : ClickableSpan() {
                            override fun onClick(v: View) {
                                userAddLine(it.groupValues[1])
                            }
                        },
                        it.groups[1]!!.range.first,
                        it.groups[1]!!.range.last + 1,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                    )
                }
            }
    }

    private fun createSpannableStringAndFormatForReceipt(s: CharSequence?): CharSequence? {
        return s
    }

    private val transaction = moshiWithCategoriesProvider.moshi.fromJson<Transaction>(savedStateHandle[KEY1])
        .also { logz("transaction:$it") }
    private val descriptionAndTotal = savedStateHandle.get<String?>(KEY2)?.let { moshiProvider.moshi.adapter<Pair<String, BigDecimal>>(Types.newParameterizedType(Pair::class.java, String::class.java, BigDecimal::class.java)).fromJson(it) }
        .also { logz("descriptionAndTotal:$it") }

    // # Events
    val navUp = MutableSharedFlow<Unit>()

    // # State
    val readoutText = MutableStateFlow<SpannableString?>(null)
    val receiptText = MutableStateFlow<CharSequence?>(null)
    val buttons =
        flowOf(
            listOf(
                ButtonVMItem(
                    title = "Submit Categorization",
                    onClick = { logz("Submit Categorization clicked") },
                ),
            )
        )
}
