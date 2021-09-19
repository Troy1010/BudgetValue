package com.tminus1010.budgetvalue._core.middleware.presentation

data class MenuVMItem(
    val title: String,
    val onClick: () -> Unit
)