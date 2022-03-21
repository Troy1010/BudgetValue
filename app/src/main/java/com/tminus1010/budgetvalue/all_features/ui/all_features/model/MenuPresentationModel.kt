package com.tminus1010.budgetvalue.all_features.ui.all_features.model

import android.view.Menu
import android.view.View

data class MenuPresentationModel(
    private val menuVMItems: List<MenuVMItem>
) {
    constructor(vararg menuVMItems: MenuVMItem?) : this(menuVMItems.toList().filterNotNull())

    fun bind(menu: Menu) {
        menu.clear()
        menuVMItems.forEach { menuItemPartial ->
            menu.add(menuItemPartial.title)
                .setOnMenuItemClickListener { menuItemPartial.onClick(); true }
        }
    }

    fun bind(view: View) {
        view.setOnCreateContextMenuListener { menu, _, _ -> bind(menu) }
    }
}