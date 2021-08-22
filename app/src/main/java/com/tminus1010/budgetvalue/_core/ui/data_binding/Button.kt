package com.tminus1010.budgetvalue._core.ui.data_binding

import android.widget.Button
import androidx.lifecycle.LifecycleOwner
import com.tminus1010.budgetvalue._core.middleware.ui.ButtonVMItem
import com.tminus1010.tmcommonkotlin.rx.extensions.observe

fun Button.bindButtonRVItem(lifecycleOwner: LifecycleOwner, buttonVMItem: ButtonVMItem) {
    text = buttonVMItem.title
    setOnClickListener { buttonVMItem.onClick() }
    buttonVMItem.onLongClick
        ?.also { setOnLongClickListener { it(); true } }
    buttonVMItem.isEnabled?.observe(lifecycleOwner) { isEnabled = it }
        ?: run { isEnabled = true }
}