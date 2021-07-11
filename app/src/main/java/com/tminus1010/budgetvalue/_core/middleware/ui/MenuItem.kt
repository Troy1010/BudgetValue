package com.tminus1010.budgetvalue._core.middleware.ui

data class MenuItem(
    val title: String,
    val onClick: () -> Unit
)