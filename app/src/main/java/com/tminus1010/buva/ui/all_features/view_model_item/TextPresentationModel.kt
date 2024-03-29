package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import com.tminus1010.tmcommonkotlin.customviews.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipeFactory
import com.tminus1010.buva.databinding.ItemHeaderBinding
import com.tminus1010.buva.databinding.ItemTextViewBinding
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import com.tminus1010.tmcommonkotlin.view.NativeText
import com.tminus1010.tmcommonkotlin.view.extensions.toPX
import kotlinx.coroutines.flow.Flow

// TODO: add functionality to TextVMItem, and use that instead. Figure out how it can be most easily unit-tested
data class TextPresentationModel(
    val style: Style = Style.ONE,
    val text1: String? = null,
    val text2: Flow<String?>? = null,
    val text3: Flow<NativeText?>? = null,
    val onClick: (() -> Unit)? = null,
    val onLongClick: (() -> Unit)? = null,
    val menuVMItems: MenuVMItems? = null,
) : ViewItemRecipeFactory {
    enum class Style { ONE, TWO, HEADER }

    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return if (style == Style.HEADER)
            ViewItemRecipe3(context, ItemHeaderBinding::inflate) { vb ->
                vb.root.text = text1
                if (text2 != null) vb.root.bind(text2) { text = it }
                if (text3 != null) vb.root.bind(text3) { text = it?.toCharSequence(context) }
                onClick?.also { onClick -> vb.root.setOnClickListener { onClick() } }
                onLongClick?.also { onLongClick -> vb.root.setOnLongClickListener { onLongClick(); true } }
                menuVMItems?.bind(vb.root)
            }
        else
            ViewItemRecipe3(context, ItemTextViewBinding::inflate) { vb ->
                if (style == Style.TWO) {
                    vb.root.setPadding(10.toPX(context), 0, 10.toPX(context), 0)
                    vb.root.requestLayout()
                }
                vb.root.text = text1
                if (text2 != null) vb.root.bind(text2) { text = it }
                if (text3 != null) vb.root.bind(text3) { text = it?.toCharSequence(context) }
                onClick?.also { onClick -> vb.root.setOnClickListener { onClick() } }
                onLongClick?.also { onLongClick -> vb.root.setOnLongClickListener { onLongClick(); true } }
                menuVMItems?.bind(vb.root)
            }
    }
}