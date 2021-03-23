package com.tminus1010.budgetvalue.middleware.ui

data class ButtonPartial(
    val title: String,
    val action: () -> Unit
)