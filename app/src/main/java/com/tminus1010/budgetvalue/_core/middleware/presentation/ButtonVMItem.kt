package com.tminus1010.budgetvalue._core.middleware.presentation

import android.widget.Button
import com.tminus1010.budgetvalue._core.all.extensions.bind
import com.tminus1010.budgetvalue._core.all.extensions.lifecycleOwner
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import io.reactivex.rxjava3.core.Observable

data class ButtonVMItem(
    val title: String? = null,
    val titleObservable: Observable<String>? = null,
    val isEnabled: Observable<Boolean>? = null,
    val userLongClick: (() -> Unit)? = null,
    val userClick: () -> Unit,
) {
    fun bind(button: Button) = button.apply {
        if (titleObservable != null)
            bind(titleObservable) { text = title }
        if (text != null)
            text = title
        setOnClickListener { userClick() }
        userLongClick
            ?.also { setOnLongClickListener { it(); true } }
        this@ButtonVMItem.isEnabled?.observe(button.lifecycleOwner!!) { isEnabled = it }
            ?: run { isEnabled = true }
    }
}