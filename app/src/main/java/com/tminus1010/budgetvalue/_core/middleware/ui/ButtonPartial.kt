package com.tminus1010.budgetvalue._core.middleware.ui

import androidx.lifecycle.LiveData
import io.reactivex.rxjava3.core.Observable

data class ButtonPartial(
    val title: String,
    val enabledLiveData: LiveData<Boolean>? = null, // Deprecated: use isEnabled instead
    val isEnabled: Observable<Boolean>? = null,
    val onLongClick: (() -> Unit)? = null,
    val onClick: () -> Unit,
)