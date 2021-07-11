package com.tminus1010.budgetvalue._core.ui.data_binding

import android.widget.Button
import androidx.lifecycle.LifecycleOwner
import com.tminus1010.budgetvalue._core.middleware.ui.ButtonItem
import com.tminus1010.tmcommonkotlin.rx.extensions.observe

fun Button.bindButtonRVItem(lifecycleOwner: LifecycleOwner, buttonItem: ButtonItem) {
    text = buttonItem.title
    setOnClickListener { buttonItem.onClick() }
    buttonItem.onLongClick
        ?.also { setOnLongClickListener { it(); true } }
    buttonItem.isEnabled?.observe(lifecycleOwner) { isEnabled = it }
        ?: run { isEnabled = true }
}