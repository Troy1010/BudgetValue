package com.tminus1010.budgetvalue._core.ui.data_binding

import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

fun TextView.databind(lifecycleOwner: LifecycleOwner, liveData: LiveData<String>) {
    liveData.observe(lifecycleOwner) { this.text = it }
}