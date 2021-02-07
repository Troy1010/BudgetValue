package com.tminus1010.budgetvalue.layer_ui

import android.app.Activity
import android.content.Intent
import com.tminus1010.budgetvalue.CODE_PICK_TRANSACTIONS_FILE

fun launchImport(activity: Activity) {
    Intent().apply { type = "*/*"; action = Intent.ACTION_GET_CONTENT }.also {
        activity.startActivityForResult(
            Intent.createChooser(it, "Select transactions csv"),
            CODE_PICK_TRANSACTIONS_FILE
        )
    }
}