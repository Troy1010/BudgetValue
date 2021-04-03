package com.tminus1010.budgetvalue._core.middleware.ui

data class ButtonPartial(
    val title: String,
    val action: () -> Unit
)