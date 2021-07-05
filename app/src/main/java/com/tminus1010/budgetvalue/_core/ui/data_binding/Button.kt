package com.tminus1010.budgetvalue._core.ui.data_binding

import android.widget.Button
import androidx.lifecycle.LifecycleOwner
import com.tminus1010.budgetvalue._core.middleware.ui.ButtonRVItem
import com.tminus1010.tmcommonkotlin.rx.extensions.observe

fun Button.bindButtonRVItem(lifecycleOwner: LifecycleOwner, buttonRVItem: ButtonRVItem) {
    text = buttonRVItem.title
    setOnClickListener { buttonRVItem.onClick() }
    buttonRVItem.onLongClick
        ?.also { setOnLongClickListener { it(); true } }
    buttonRVItem.isEnabled?.observe(lifecycleOwner) { isEnabled = it }
        ?: run { isEnabled = true }
}