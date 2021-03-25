package com.tminus1010.budgetvalue.middleware.ui

class MenuItemPartial(
    val title: String,
    val action: () -> Unit
) {
    val id: Int = counter++
    companion object {
        private var counter = 0
    }
}