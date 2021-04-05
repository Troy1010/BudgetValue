package com.tminus1010.budgetvalue._core.ui.data_binding

import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.tminus1010.tmcommonkotlin.misc.fnName
import com.tminus1010.tmcommonkotlin.misc.logz

fun TextView.bindText(liveData: LiveData<String>, lifecycleOwner: LifecycleOwner? = null) {
    (lifecycleOwner ?: findViewTreeLifecycleOwner())
        ?.also { liveData.observe(it) { this.text = it } }
        ?: logz("WARNING: $fnName failed to find lifecycleOwner")
}

fun TextView.bindEnabled(lifecycleOwner: LifecycleOwner, liveData: LiveData<Boolean>) {
    liveData.observe(lifecycleOwner) { this.isEnabled = it }
}