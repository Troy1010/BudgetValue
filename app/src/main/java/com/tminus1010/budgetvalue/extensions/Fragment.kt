package com.tminus1010.budgetvalue.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.tminus1010.tmcommonkotlin.misc.createVmFactory

// * Must be inlined to support different VMs
inline fun <reified VM : ViewModel> Fragment.activityViewModels2(noinline function: () -> VM): Lazy<VM> {
    return this.activityViewModels { createVmFactory(function) }
}

// * Must be inlined to support different VMs
inline fun <reified VM : ViewModel> Fragment.viewModels2(noinline function: () -> VM): Lazy<VM> {
    return this.viewModels { createVmFactory(function) }
}
