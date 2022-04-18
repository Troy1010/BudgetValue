package com.tminus1010.buva.framework.android.tmTableView3

import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

@Deprecated("use commonlib's TMTableView")
interface IViewItemRecipe3 {
    val intrinsicWidth: Int
    val intrinsicHeight: Int
    fun createVB(): ViewBinding
    fun createVB(viewGroup: ViewGroup?): ViewBinding
    fun bind(vb: ViewBinding)
    fun createImpatientlyBoundView(): View
    fun intrinsicHeight(width: Int): Int
}