package com.tminus1010.budgetvalue._core.extensions

import android.view.Menu
import com.tminus1010.budgetvalue._core.middleware.ui.MenuVMItem

fun Menu.add(vararg menuVMItems: MenuVMItem) {
    menuVMItems.forEach { menuItemPartial -> add(menuItemPartial.title).setOnMenuItemClickListener { menuItemPartial.onClick(); true } }
}

fun Menu.add(menuVMItems: List<MenuVMItem>) {
    menuVMItems.forEach { menuItemPartial -> add(menuItemPartial.title).setOnMenuItemClickListener { menuItemPartial.onClick(); true } }
}