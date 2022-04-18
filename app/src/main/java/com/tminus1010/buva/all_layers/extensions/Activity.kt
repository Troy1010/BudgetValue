package com.tminus1010.buva.all_layers.extensions

import androidx.appcompat.app.AppCompatActivity
import com.tminus1010.buva.framework.android.ShowAlertDialog

val AppCompatActivity.showAlertDialog get() = ShowAlertDialog(this)