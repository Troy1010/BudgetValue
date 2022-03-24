package com.tminus1010.budgetvalue.ui.all_features.model

import android.content.Context
import androidx.appcompat.app.AlertDialog

class PopupVMItem(
    private val msg: String,
    private val onYes: () -> Unit,
    private val onNo: () -> Unit = {},
) {
    fun show(context: Context) {
        AlertDialog.Builder(context)
            .setMessage(msg)
            .setPositiveButton("Yes") { _, _ -> onYes() }
            .setNegativeButton("No") { _, _ -> onNo() }
            .show()
    }
}