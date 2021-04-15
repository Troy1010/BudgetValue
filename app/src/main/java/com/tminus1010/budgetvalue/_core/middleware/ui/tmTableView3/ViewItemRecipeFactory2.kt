package com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3

import androidx.viewbinding.ViewBinding

class ViewItemRecipeFactory2<VB : ViewBinding, D : Any?>(
    val vbLambda: () -> VB,
    val bindLambda: (VB, D) -> Unit,
) {
    fun createMany(datas: Iterable<D>) = datas.map { ViewItemRecipe2(vbLambda, it, bindLambda) }
    fun createOne(data: D) = ViewItemRecipe2(vbLambda, data, bindLambda)
}