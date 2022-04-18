package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import android.widget.Button
import com.tminus1010.buva.all_layers.extensions.getColorByAttr
import com.tminus1010.buva.databinding.ItemCategoryBtnBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.ViewItemRecipe3
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow

data class ButtonVMItem2(
    val title: String? = null,
    val titleObservable: Observable<String>? = null,
    val isEnabled: Observable<Boolean>? = null,
    val isEnabled2: Flow<Boolean>? = null,
    val alpha: Flow<Float>? = null,
    val backgroundColor: Int? = null,
    val menuVMItems: MenuVMItems? = null,
    val menuVMItemsFlow: Flow<MenuVMItems>? = null,
    val onLongClick: (() -> Unit)? = null,
    val onClick: () -> Unit,
) : IHasToViewItemRecipe {
    fun bind(button: Button) {
        button.text = title
        if (titleObservable != null)
            button.bind(titleObservable) { text = title }
        if (isEnabled != null)
            button.bind(isEnabled) { button.isEnabled = it }
        if (isEnabled2 != null)
            button.bind(isEnabled2) { button.isEnabled = it }
        if (alpha != null)
            button.bind(alpha) { alpha = it }
        if (backgroundColor != null)
            button.setBackgroundColor(button.context.theme.getColorByAttr(backgroundColor))
        menuVMItems?.bind(button)
        if (menuVMItemsFlow != null)
            button.bind(menuVMItemsFlow) { it.bind(this) }
        button.setOnClickListener { onClick() }
        onLongClick?.also { button.setOnLongClickListener { it(); true } }
    }

    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3(context, ItemCategoryBtnBinding::inflate, ItemCategoryBtnBinding::inflate) { vb ->
            bind(vb.btnCategory)
        }
    }
}