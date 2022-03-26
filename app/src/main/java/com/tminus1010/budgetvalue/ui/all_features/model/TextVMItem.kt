package com.tminus1010.budgetvalue.ui.all_features.model

import android.content.Context
import android.widget.TextView
import com.tminus1010.budgetvalue.all_layers.extensions.getColorByAttr
import com.tminus1010.budgetvalue.databinding.ItemHeaderBinding
import com.tminus1010.budgetvalue.databinding.ItemTextViewBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.ViewItemRecipe3
import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow

class TextVMItem(
    val text1: String? = null,
    val text2: Observable<Box<String?>>? = null,
    val text3: Flow<String?>? = null,
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