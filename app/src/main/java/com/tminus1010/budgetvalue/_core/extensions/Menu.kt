package com.tminus1010.budgetvalue._core.extensions

import android.view.Menu
import com.tminus1010.budgetvalue._core.middleware.ui.MenuItemPartial

@Deprecated("Use add2")
fun Menu.add(vararg menuItemPartials: MenuItemPartial) {
    menuItemPartials.forEach { add(Menu.NONE, it.id, Menu.NONE, it.title) }
}

fun Menu.add2(vararg menuItemPartials: MenuItemPartial) {
    menuItemPartials.forEach { (title, action) -> add(title).setOnMenuItemClickListener { action(); true } }
}