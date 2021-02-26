package com.tminus1010.budgetvalue.extensions

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.dependency_injection.view_model_provision.IVMComponent
import com.tminus1010.budgetvalue.dependency_injection.view_model_provision.getVMProvisionMethod
import com.tminus1010.tmcommonkotlin.view.createViewModelFactory

inline fun <reified VM : ViewModel> AppCompatActivity.viewModelsWithDagger(vmComponent: IVMComponent): Lazy<VM> =
    vmComponent.getVMProvisionMethod<VM>() // get provision method immediately to trigger errors earlier.
        .let { viewModels { createViewModelFactory { it.invoke(vmComponent) as VM } } }