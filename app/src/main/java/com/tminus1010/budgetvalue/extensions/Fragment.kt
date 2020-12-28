package com.tminus1010.budgetvalue.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.tminus1010.tmcommonkotlin.misc.createVmFactory

fun <VM> Fragment.activityViewModels2(function: () -> VM): Lazy<VM> {
    return this.activityViewModels { createVmFactory { function() as ViewModel } }
}

fun <VM> Fragment.viewModels2(function: () -> VM): Lazy<VM> {
    return this.viewModels { createVmFactory { function() as ViewModel } }
}
