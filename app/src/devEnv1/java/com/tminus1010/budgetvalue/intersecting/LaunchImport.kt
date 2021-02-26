package com.tminus1010.budgetvalue.layer_ui

import android.app.Activity
import android.content.Intent
import com.tminus1010.budgetvalue.CODE_PICK_TRANSACTIONS_FILE
import com.tminus1010.budgetvalue.MockImportSelectionActivity

fun launchImport(activity: Activity) {
    Intent(activity, MockImportSelectionActivity::class.java)
        .also { activity.startActivity(it) }
}