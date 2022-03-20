package com.tminus1010.budgetvalue._core.all_layers.extensions

import android.view.Menu
import androidx.core.view.iterator
import com.tminus1010.budgetvalue._core.presentation.model.MenuVMItem

fun Menu.add(vararg menuVMItems: MenuVMItem) {
    menuVMItems.forEach { menuItemPartial -> add(menuItemPartial.title).setOnMenuItemClickListener { menuItemPartial.onClick(); true } }
}

fun Menu.add(menuVMItems: List<MenuVMItem>) {
    menuVMItems.forEach { menuItemPartial -> add(menuItemPartial.title).setOnMenuItemClickListener { menuItemPartial.onClick(); true } }
}

val Menu.items get() = iterator().asSequence()

fun Menu.unCheckAllMenuItems() {
    items.forEach { item ->
        if (item.hasSubMenu())
            item.subMenu.unCheckAllMenuItems()
        else
            item.isChecked = false
    }
}