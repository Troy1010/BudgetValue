package com.tminus1010.budgetvalue.all_layers.extensions

import android.content.Context
import androidx.appcompat.app.AlertDialog

fun Context.easyAlertDialog(s: CharSequence) {
    AlertDialog.Builder(this)
        .setMessage(s)
        .setNeutralButton("Okay") { _, _ -> }
        .setCancelable(false)
        .show()
}