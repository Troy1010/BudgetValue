package com.tminus1010.budgetvalue._core.middleware.view.tmTableView

import android.view.View

interface IViewItemRecipe {
    val intrinsicWidth: Int
    val intrinsicHeight: Int
    fun createView(): View
    fun bindView(view: View)
    fun createBoundView(): View
}