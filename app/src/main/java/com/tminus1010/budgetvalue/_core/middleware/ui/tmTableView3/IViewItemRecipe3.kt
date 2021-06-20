package com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

interface IViewItemRecipe3 {
    val intrinsicWidth: Int
    val intrinsicHeight: Int
    fun createVB(): ViewBinding
    fun bind(vb: ViewBinding, lifecycle: LifecycleOwner)
    fun bindImpatiently(vb: ViewBinding)
    fun createImpatientlyBoundView(): View
}