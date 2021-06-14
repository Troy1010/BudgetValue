package com.tminus1010.budgetvalue._core.ui.data_binding

import android.widget.Button
import androidx.lifecycle.LifecycleOwner
import com.tminus1010.budgetvalue._core.middleware.ui.ButtonPartial
import com.tminus1010.tmcommonkotlin.rx.extensions.observe

fun Button.bindButtonPartial(lifecycleOwner: LifecycleOwner, buttonPartial: ButtonPartial) {
    text = buttonPartial.title
    setOnClickListener { buttonPartial.onClick() }
    if (buttonPartial.enabledLiveData != null)
        bindEnabled(lifecycleOwner, buttonPartial.enabledLiveData)
    buttonPartial.onLongClick
        ?.also { setOnLongClickListener { it(); true } }
    buttonPartial.isEnabled?.observe(lifecycleOwner) { isEnabled = it }
        ?: run { isEnabled = true }
}