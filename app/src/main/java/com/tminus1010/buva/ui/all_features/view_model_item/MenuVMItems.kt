package com.tminus1010.buva.ui.all_features.view_model_item

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText

data class MenuVMItems(
    private val menuVMItems: List<MenuVMItem>,
) {
    constructor(vararg menuVMItems: MenuVMItem?) : this(menuVMItems.toList().filterNotNull())

    fun bind(menu: Menu) {
        menu.removeGroup(2461)
        menuVMItems.forEach { menuVMItem ->
            menu.add(2461, 0, 0, menuVMItem.title)
                .setOnMenuItemClickListener { menuVMItem.onClick(); true }
        }
    }

    fun bind(view: View) {
        when (view) {
            is EditText ->
                object : ActionMode.Callback2() {
                    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                        menuVMItems.withIndex().forEach { (i, menuVMItem) ->
                            val id = i + 1487
                            menu.add(48651, id, 0, menuVMItem.title)
                                .setOnMenuItemClickListener { menuVMItem.onClick(); true }
                        }
                        return true
                    }
                    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                        return false
                    }
                    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                        return false
                    }
                    override fun onDestroyActionMode(mode: ActionMode?) {

                    }
                }
                    .also {
                        view.customInsertionActionModeCallback = it
                        view.customSelectionActionModeCallback = it
                    }
            else ->
                view.setOnCreateContextMenuListener { menu, _, _ -> bind(menu) }

        }
    }
}