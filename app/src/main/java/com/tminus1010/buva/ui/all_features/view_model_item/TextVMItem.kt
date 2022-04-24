package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import android.widget.TextView
import com.tminus1010.buva.all_layers.extensions.getColorByAttr
import com.tminus1010.buva.databinding.ItemHeaderBinding
import com.tminus1010.buva.databinding.ItemTextViewBinding
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import com.tminus1010.tmcommonkotlin.customviews.IHasToViewItemRecipe
import com.tminus1010.tmcommonkotlin.customviews.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipe3
import com.tminus1010.tmcommonkotlin.tuple.Box
import com.tminus1010.tmcommonkotlin.view.NativeText
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow

class TextVMItem(
    val text1: String? = null,
    val text2: Observable<Box<String?>>? = null,
    val text3: Flow<String?>? = null,
    val text4: Flow<NativeText?>? = null,
    val onClick: (() -> Unit)? = null,
    val menuVMItems: MenuVMItems? = null,
    val backgroundColor: Int? = null,
    val style: Style = Style.ONE,
) : IHasToViewItemRecipe {
    enum class Style { ONE, TWO, HEADER }

    fun bind(textView: TextView) {
        textView.text = text1
        text2?.also { textView.bind(text2) { text = it.first } }
        text3?.also { textView.bind(text3) { text = it } }
        text4?.also { textView.bind(text4) { text = it?.toCharSequence(this.context) } }
        textView.setOnClickListener { onClick?.invoke() }
        menuVMItems?.bind(textView)
        backgroundColor?.also { textView.setBackgroundColor(textView.context.theme.getColorByAttr(backgroundColor)) }
    }

    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        if (style == Style.TWO) TODO()
        return if (style == Style.HEADER)
            ViewItemRecipe3(context, ItemHeaderBinding::inflate) { vb -> bind(vb.textview) }
        else
            ViewItemRecipe3(context, ItemTextViewBinding::inflate) { vb -> bind(vb.textview) }
    }
}