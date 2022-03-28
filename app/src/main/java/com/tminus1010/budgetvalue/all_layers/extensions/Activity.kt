package com.tminus1010.budgetvalue.all_layers.extensions

import androidx.appcompat.app.AppCompatActivity
import com.tminus1010.budgetvalue.framework.android.ShowAlertDialog

val AppCompatActivity.showAlertDialog get() = ShowAlertDialog(this)