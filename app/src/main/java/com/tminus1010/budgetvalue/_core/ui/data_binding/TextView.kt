package com.tminus1010.budgetvalue._core.ui.data_binding

import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.tminus1010.budgetvalue._core.extensions.observe2
import com.tminus1010.tmcommonkotlin.misc.extensions.easyGetLayoutParams
import com.tminus1010.tmcommonkotlin.misc.fnName

fun TextView.bindText(liveData: LiveData<String>, lifecycle: LifecycleOwner? = null) {
    (lifecycle ?: findViewTreeLifecycleOwner())
        ?.also {
            liveData.observe2(it) { s ->
                easyGetLayoutParams() // You might get: "Attempt to read from field 'int android.view.ViewGroup$LayoutParams.width' on a null object reference" without this.
                this.text = s
            }
        }
        ?: logz("WARNING: $fnName failed to find lifecycleOwner")
}

fun TextView.bindEnabled(lifecycleOwner: LifecycleOwner, liveData: LiveData<Boolean>) {
    liveData.observe(lifecycleOwner) { this.isEnabled = it }
}