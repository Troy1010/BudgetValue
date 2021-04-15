package com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3

import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

class ViewItemRecipeFactory2<VB : ViewBinding, D : Any?>(
    val createVB: () -> VB,
    val bind: (VB, D, LifecycleOwner) -> Unit,
) {
    fun createMany(datas: Iterable<D>) = datas.map { ViewItemRecipe2(createVB, it, bind) }
    fun createOne(data: D) = ViewItemRecipe2(createVB, data, bind)
}