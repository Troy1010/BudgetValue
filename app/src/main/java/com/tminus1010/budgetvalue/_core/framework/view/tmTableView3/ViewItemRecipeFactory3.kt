package com.tminus1010.budgetvalue._core.framework.view.tmTableView3

import android.view.LayoutInflater
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

open class ViewItemRecipeFactory3<VB : ViewBinding, D : Any?>(
    private val createVB: () -> VB,
    private val bind: (D, VB, LifecycleOwner) -> Unit,
) {
    constructor(layoutInflater: LayoutInflater, bindingInflater: (LayoutInflater) -> VB, bind: (D, VB, LifecycleOwner) -> Unit) : this({ bindingInflater(layoutInflater) }, bind)

    fun createMany(datas: Iterable<D>) = datas.map { ViewItemRecipe3(createVB, bind, it) }
    fun createOne(data: D) = ViewItemRecipe3(createVB, bind, data)
}