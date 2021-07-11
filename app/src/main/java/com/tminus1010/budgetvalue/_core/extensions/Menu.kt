package com.tminus1010.budgetvalue._core.extensions

import android.view.Menu
import com.tminus1010.budgetvalue._core.middleware.ui.MenuItem

fun Menu.add(vararg menuItems: MenuItem) {
    menuItems.forEach { menuItemPartial -> add(menuItemPartial.title).setOnMenuItemClickListener { menuItemPartial.onClick(); true } }
}