package com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3

import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

open class ViewItemRecipeFactory3<VB : ViewBinding, D : Any?>(
    val createVB: () -> VB,
    val bind: (D, VB, LifecycleOwner) -> Unit,
) {
    fun createMany(datas: Iterable<D>) = datas.map { ViewItemRecipe3(createVB, it, bind) }
    fun createOne(data: D) = ViewItemRecipe3(createVB, data, bind)
}