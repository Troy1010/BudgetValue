package com.tminus1010.budgetvalue.framework.androidx.tmTableView

import android.view.View

interface IViewItemRecipe {
    val intrinsicWidth: Int
    val intrinsicHeight: Int
    fun createView(): View
    fun bindView(view: View)
    fun createBoundView(): View
}