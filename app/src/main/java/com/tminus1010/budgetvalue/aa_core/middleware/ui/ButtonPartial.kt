package com.tminus1010.budgetvalue.aa_core.middleware.ui

data class ButtonPartial(
    val title: String,
    val action: () -> Unit
)