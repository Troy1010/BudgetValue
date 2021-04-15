package com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3

import android.view.View
import androidx.viewbinding.ViewBinding

interface IViewItemRecipe2 {
    val intrinsicWidth: Int
    val intrinsicHeight: Int
    fun createView(): ViewBinding
    fun bind(vb: ViewBinding)
    fun createBoundView(): View
}