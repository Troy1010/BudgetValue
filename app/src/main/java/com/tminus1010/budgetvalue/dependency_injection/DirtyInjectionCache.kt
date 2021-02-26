package com.tminus1010.budgetvalue.dependency_injection

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.dependency_injection.view_model_provision.IVMComponent
import com.tminus1010.budgetvalue.extensions.viewModelsWithDagger

class DirtyInjectionCache(val activity: AppCompatActivity, val vmComponent: IVMComponent) {
    constructor(activity: FragmentActivity, vmComponent: IVMComponent) :
            this(activity as AppCompatActivity, vmComponent)

    val cache = hashMapOf<Class<*>, Lazy<ViewModel>>()

    inline fun <reified VM : ViewModel> get(): VM =
        (cache[VM::class.java] ?: activity.viewModelsWithDagger<VM>(vmComponent)
            .also { cache[VM::class.java] = it })
            .value as VM
}
