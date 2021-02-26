package com.tminus1010.budgetvalue.dependency_injection

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.tminus1010.tmcommonkotlin.view.createViewModelFactory

class ViewModelFactoriesHelper(val activity: AppCompatActivity, val component: AppComponent) {
    constructor(activity: FragmentActivity, vmComponent: AppComponent) :
            this(activity as AppCompatActivity, vmComponent)

    // This cache gives backing fields to the interface
    val cache = hashMapOf<Class<*>, Lazy<ViewModel>>()

    inline fun <reified VM : ViewModel> get(noinline action: () -> VM): VM =
        cache[VM::class.java]?.value as? VM
            ?: activity.viewModels<VM> { createViewModelFactory(action) }
                .also { cache[VM::class.java] = it }.value
}
