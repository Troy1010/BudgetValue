package com.tminus1010.budgetvalue._core.middleware.view

import android.widget.Button
import com.tminus1010.budgetvalue._core.extensions.lifecycleOwner
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import io.reactivex.rxjava3.core.Observable

data class ButtonVMItem(
    val title: String,
    val isEnabled: Observable<Boolean>? = null,
    val onLongClick: (() -> Unit)? = null,
    val onClick: () -> Unit,
) {
    fun bind(button: Button) = button.apply {
        text = title
        setOnClickListener { onClick() }
        onLongClick
            ?.also { setOnLongClickListener { it(); true } }
        this@ButtonVMItem.isEnabled?.observe(button.lifecycleOwner!!) { isEnabled = it }
            ?: run { isEnabled = true }
    }
}