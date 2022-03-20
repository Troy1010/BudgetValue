package com.tminus1010.budgetvalue._core.all_layers.extensions

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.tminus1010.budgetvalue._core.presentation.model.UnformattedString

fun Context.easyAlertDialog(s: String) {
    AlertDialog.Builder(this)
        .setMessage(s)
        .setNeutralButton("Okay") { _, _ -> }
        .setCancelable(false)
        .show()
}

fun Context.getString(unformattedString: UnformattedString) = unformattedString.getString(this)