package com.tminus1010.budgetvalue._core.all.extensions

import androidx.appcompat.widget.PopupMenu
import com.tminus1010.budgetvalue._core.presentation.model.MenuVMItem

fun PopupMenu.show(menuVMItems: List<MenuVMItem>) {
    menu.clear()
    menu.add(*menuVMItems.toTypedArray())
    show()
}