package com.tminus1010.budgetvalue._core.middleware.ui

import androidx.lifecycle.LiveData

data class ButtonPartial(
    val title: String,
    val enabledLiveData: LiveData<Boolean>? = null,
    val onLongClick: (() -> Unit)? = null,
    val onClick: () -> Unit,
)