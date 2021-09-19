package com.tminus1010.budgetvalue._core.view.extensions

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.tminus1010.budgetvalue._core.view.view_model_items.UnformattedString

fun Context.easyAlertDialog(s: String) {
    AlertDialog.Builder(this)
        .setMessage(s)
        .setNeutralButton("Okay") { _, _ -> }
        .setCancelable(false)
        .show()
}

fun Context.getString(unformattedString: UnformattedString) = unformattedString.getString(this)