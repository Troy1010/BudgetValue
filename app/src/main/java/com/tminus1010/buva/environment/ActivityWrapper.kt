package com.tminus1010.buva.environment

import android.app.Activity
import com.tminus1010.tmcommonkotlin.androidx.ShowAlertDialog
import javax.inject.Inject

class ActivityWrapper(activity: Activity) {
    @Inject
    constructor() : this(activity ?: error("This class expects that an Activity is assigned to global companion object variable before construction"))

    val showAlertDialog = ShowAlertDialog(activity)

    companion object {
        // This pattern can easily cause memory leaks. Use with care.
        var activity: Activity? = null
    }
}