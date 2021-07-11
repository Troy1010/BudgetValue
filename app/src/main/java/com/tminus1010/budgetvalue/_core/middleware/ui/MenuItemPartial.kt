package com.tminus1010.budgetvalue._core.middleware.ui

data class MenuItemPartial(
    val title: String,
    val lambda: () -> Unit
)