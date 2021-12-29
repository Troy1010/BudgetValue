package com.tminus1010.budgetvalue._core.presentation.model

data class MenuVMItem(
    val title: String,
    val onClick: () -> Unit
)