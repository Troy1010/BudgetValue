package com.tminus1010.budgetvalue._core.ui.data_binding

import android.widget.Button
import androidx.lifecycle.LifecycleOwner
import com.tminus1010.budgetvalue._core.middleware.ui.ButtonPartial

fun Button.bindButtonPartial(lifecycleOwner: LifecycleOwner, buttonPartial: ButtonPartial) {
    text = buttonPartial.title
    setOnClickListener { buttonPartial.onClick() }
    if (buttonPartial.enabledLiveData != null)
        bindEnabled(lifecycleOwner, buttonPartial.enabledLiveData)
    if (buttonPartial.onLongClick != null)
        setOnLongClickListener { buttonPartial.onLongClick!!(); true }
}