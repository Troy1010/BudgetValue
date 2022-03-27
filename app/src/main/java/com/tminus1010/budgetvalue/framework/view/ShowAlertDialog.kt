package com.tminus1010.budgetvalue.framework.view

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.Reusable
import javax.inject.Inject

@Reusable
class ShowAlertDialog @Inject constructor() {
    /**
     * Must use [Activity] or you will see: java.lang.IllegalStateException: You need to use a Theme.AppCompat theme (or descendant) with this activity.
     */
    operator fun invoke(activity: Activity, body: NativeText) {
        AlertDialog.Builder(activity)
            .setMessage(body.toCharSequence(activity))
            .setPositiveButton("Okay") { _, _ -> Unit }
            .show()
    }

    operator fun invoke(activity: Activity, body: String) {
        invoke(activity, NativeText.Simple(body))
    }
}