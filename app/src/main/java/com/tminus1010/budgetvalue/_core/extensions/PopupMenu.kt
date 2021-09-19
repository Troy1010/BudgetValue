package com.tminus1010.budgetvalue._core.extensions

import androidx.appcompat.widget.PopupMenu
import com.tminus1010.budgetvalue._core.middleware.presentation.MenuVMItem

fun PopupMenu.show(menuVMItems: List<MenuVMItem>) {
    menu.clear()
    menu.add(*menuVMItems.toTypedArray())
    show()
}