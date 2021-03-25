package com.tminus1010.budgetvalue.extensions

import androidx.appcompat.widget.PopupMenu
import com.tminus1010.budgetvalue.middleware.ui.MenuItemPartial

fun PopupMenu.show(menuItemPartials: List<MenuItemPartial>) {
    menu.clear()
    menu.add(*menuItemPartials.toTypedArray())
    setOnMenuItemClickListener {
        menuItemPartials.find { it.id == it.id }!!.action()
        true
    }
    show()
}