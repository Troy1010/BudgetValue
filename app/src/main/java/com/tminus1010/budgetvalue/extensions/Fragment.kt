package com.tminus1010.budgetvalue.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.appComponent
import com.tminus1010.budgetvalue.dependency_injection.view_model_provision.IVMComponent
import com.tminus1010.budgetvalue.dependency_injection.view_model_provision.getVMProvisionMethod
import com.tminus1010.tmcommonkotlin.view.createViewModelFactory

inline fun <reified VM : ViewModel> Fragment.activityViewModelsWithDagger(vmComponent: IVMComponent): Lazy<VM> =
    vmComponent.getVMProvisionMethod<VM>() // get provision method immediately to trigger errors earlier.
        .let { activityViewModels { createViewModelFactory { it.invoke(vmComponent) as VM } } }

inline fun <reified VM : ViewModel> Fragment.viewModelsWithDagger(vmComponent: IVMComponent): Lazy<VM> =
    vmComponent.getVMProvisionMethod<VM>() // get provision method immediately to trigger errors earlier.
        .let { viewModels { createViewModelFactory { it.invoke(vmComponent) as VM } } }

// This can be used if you don't care to get provision method immediately to trigger errors earlier, and a small
// performance hit b/c you need to re-create the lazy every time.
inline fun <reified VM : ViewModel> Fragment.avm(vmComponent: IVMComponent) =
    activityViewModelsWithDagger<VM>(vmComponent).value

// This can be used if you don't care to get provision method immediately to trigger errors earlier, and a small
// performance hit b/c you need to re-create the lazy every time.
inline fun <reified VM : ViewModel> Fragment.vm(vmComponent: IVMComponent) =
    viewModelsWithDagger<VM>(vmComponent).value