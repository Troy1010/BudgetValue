package com.tminus1010.budgetvalue.all_layers.extensions

import androidx.appcompat.widget.PopupMenu
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.MenuVMItem

fun PopupMenu.show(menuVMItems: List<MenuVMItem>) {
    menu.clear()
    menu.add(*menuVMItems.toTypedArray())
    show()
}