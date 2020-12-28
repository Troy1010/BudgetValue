package com.tminus1010.budgetvalue.extensions

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.tminus1010.tmcommonkotlin.misc.createVmFactory

// * Must be inlined to support different VMs
inline fun <reified VM : ViewModel> AppCompatActivity.viewModels2(noinline function: () -> VM): Lazy<VM> {
    return this.viewModels { createVmFactory(function) }
}