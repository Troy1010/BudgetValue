package com.tminus1010.budgetvalue.intersecting

import android.app.Activity
import android.content.Intent
import com.tminus1010.budgetvalue.MockImportSelectionActivity

fun launchImport(activity: Activity) {
    Intent(activity, MockImportSelectionActivity::class.java)
        .also { activity.startActivity(it) }
}