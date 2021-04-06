package com.tminus1010.budgetvalue._core.extensions

import android.view.Menu
import com.tminus1010.budgetvalue._core.middleware.ui.MenuItemPartial

fun Menu.add(vararg menuItemPartials: MenuItemPartial) {
    menuItemPartials.withIndex().forEach { (i, it) -> add(0, it.id, i, it.title) }
}