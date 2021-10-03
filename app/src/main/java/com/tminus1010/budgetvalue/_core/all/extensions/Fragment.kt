package com.tminus1010.budgetvalue._core.all.extensions

import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.tminus1010.budgetvalue._core.middleware.view.tmTableView3.ViewItemRecipe3_
import kotlin.reflect.full.functions

@Suppress("UNCHECKED_CAST")
inline fun <reified VB : ViewBinding, D : Any?> Fragment.viewItemRecipe(noinline bind: (D, VB) -> Unit, d: D): ViewItemRecipe3_<VB, D> {
    // TODO("this type of reflection will fail after obfuscation")
    val inflate = VB::class.functions.find { it.name == "inflate" }!! as (LayoutInflater) -> VB
    return ViewItemRecipe3_(requireContext(), inflate, bind, d)
}

inline fun <reified VB : ViewBinding> Fragment.viewItemRecipe(noinline bind: (Unit, VB) -> Unit): ViewItemRecipe3_<VB, Unit> {
    return viewItemRecipe(bind, Unit)
}