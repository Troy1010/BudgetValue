package com.tminus1010.budgetvalue._core.extensions

import android.view.Menu
import com.tminus1010.budgetvalue._core.middleware.ui.MenuItemPartial

fun Menu.add(vararg menuItemPartials: MenuItemPartial) {
    menuItemPartials.forEach { menuItemPartial -> add(menuItemPartial.title).setOnMenuItemClickListener { menuItemPartial.lambda(); true } }
}