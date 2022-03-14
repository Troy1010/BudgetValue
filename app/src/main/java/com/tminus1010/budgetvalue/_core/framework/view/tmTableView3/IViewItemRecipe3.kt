package com.tminus1010.budgetvalue._core.framework.view.tmTableView3

import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

interface IViewItemRecipe3 {
    val intrinsicWidth: Int
    val intrinsicHeight: Int
    fun createVB(): ViewBinding
    fun createVB(viewGroup: ViewGroup?): ViewBinding
    fun bind(vb: ViewBinding)
    fun createImpatientlyBoundView(): View
    fun intrinsicHeight(width: Int): Int
}