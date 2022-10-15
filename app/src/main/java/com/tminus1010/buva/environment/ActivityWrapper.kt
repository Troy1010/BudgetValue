package com.tminus1010.buva.environment

import android.app.Activity
import com.tminus1010.tmcommonkotlin.androidx.ShowAlertDialog
import javax.inject.Inject

class ActivityWrapper @Inject constructor() {
    private val activity get() = Companion.activity ?: error("This class expects that ActivityWrapper.activity is assigned")

    val showAlertDialog get() = ShowAlertDialog(activity)

    companion object {
        // This pattern can cause memory leaks. However, this project only has 1 Activity, so a memory leak is unlikely
        var activity: Activity? = null
    }
}