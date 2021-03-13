package com.tminus1010.budgetvalue.layer_ui.misc

class MenuItemPartial(
    val title: String,
    val action: () -> Unit
) {
    val id: Int = counter++
    companion object {
        private var counter = 0
    }
}