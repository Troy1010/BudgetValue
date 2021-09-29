package com.tminus1010.budgetvalue._core.presentation_and_view._view_model_items

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