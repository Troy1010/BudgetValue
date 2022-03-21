package com.tminus1010.budgetvalue.all_features.all_layers.extensions

import androidx.appcompat.widget.PopupMenu
import com.tminus1010.budgetvalue.all_features.ui.all_features.model.MenuVMItem

fun PopupMenu.show(menuVMItems: List<MenuVMItem>) {
    menu.clear()
    menu.add(*menuVMItems.toTypedArray())
    show()
}