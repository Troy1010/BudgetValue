package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import android.widget.Button
import com.tminus1010.buva.databinding.ItemButtonBinding
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import com.tminus1010.tmcommonkotlin.androidx.extensions.lifecycleOwner
import com.tminus1010.tmcommonkotlin.customviews.IHasToViewItemRecipe
import com.tminus1010.tmcommonkotlin.customviews.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipe3
import com.tminus1010.tmcommonkotlin.rx3.extensions.observe
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow

data class ButtonVMItem(
    val title: String? = null,
    // title2 does not seem to work..?
    val title2: Flow<String?>? = null,
    val isEnabled: Observable<Boolean>? = null,
    val isEnabled2: Flow<Boolean>? = null,
    val alpha: Flow<Float>? = null,
    val onLongClick: (() -> Unit)? = null,
    val onClick: () -> Unit,
) : IHasToViewItemRecipe {
    fun bind(button: Button) = button.apply {
        if (title2 != null)
            bind(title2) { text = it.logx("ButtonVMItem setting text"); requestLayout() }
        if (text != null)
            text = title
        if (this@ButtonVMItem.alpha != null)
            bind(this@ButtonVMItem.alpha) { alpha = it }
        setOnClickListener { onClick() }
        onLongClick?.also { setOnLongClickListener { it(); true } }
        this@ButtonVMItem.isEnabled?.observe(button.lifecycleOwner!!) { isEnabled = it }
            ?: run { isEnabled = true }
        isEnabled2?.observe(button.lifecycleOwner!!) { isEnabled = it }
            ?: run { isEnabled = true }
    }

    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3(context, ItemButtonBinding::inflate) { vb ->
            bind(vb.btnItem)
        }
    }
}