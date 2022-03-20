package com.tminus1010.budgetvalue.all_features.presentation.model

data class MenuVMItem(
    val title: String,
    val onClick: () -> Unit
)