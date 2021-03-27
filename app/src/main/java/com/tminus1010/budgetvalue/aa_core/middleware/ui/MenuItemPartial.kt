package com.tminus1010.budgetvalue.aa_core.middleware.ui

class MenuItemPartial(
    val title: String,
    val action: () -> Unit
) {
    val id: Int = counter++
    companion object {
        private var counter = 0
    }
}