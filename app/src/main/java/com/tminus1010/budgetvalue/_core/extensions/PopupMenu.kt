package com.tminus1010.budgetvalue._core.extensions

import androidx.appcompat.widget.PopupMenu
import com.tminus1010.budgetvalue._core.middleware.ui.MenuItem

fun PopupMenu.show(menuItems: List<MenuItem>) {
    menu.clear()
    menu.add(*menuItems.toTypedArray())
    show()
}