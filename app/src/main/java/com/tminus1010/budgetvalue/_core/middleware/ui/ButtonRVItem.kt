package com.tminus1010.budgetvalue._core.middleware.ui

import io.reactivex.rxjava3.core.Observable

data class ButtonRVItem(
    val title: String,
    val isEnabled: Observable<Boolean>? = null,
    val onLongClick: (() -> Unit)? = null,
    val onClick: () -> Unit,
)