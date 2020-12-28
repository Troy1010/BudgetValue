package com.tminus1010.budgetvalue.extensions

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.tminus1010.tmcommonkotlin.misc.createVmFactory

fun <VM> AppCompatActivity.viewModels2(function: () -> VM): Lazy<VM> {
    return this.viewModels { createVmFactory { function() as ViewModel } }
}