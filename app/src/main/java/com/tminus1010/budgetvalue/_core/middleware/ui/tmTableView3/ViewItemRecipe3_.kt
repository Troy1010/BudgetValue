package com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.tminus1010.budgetvalue._core.middleware.ui.ExposedLifecycleOwner
import com.tminus1010.tmcommonkotlin.misc.extensions.measureUnspecified
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable

data class ViewItemRecipe3_<VB : ViewBinding, D : Any?> constructor(
    val context: Context,
    val inflate: (LayoutInflater) -> VB,
    val bind: ((D, VB, LifecycleOwner) -> Unit),
    val d: D,
) : IViewItemRecipe3 {
    override val intrinsicWidth: Int
        get() = createImpatientlyBoundView().apply { measureUnspecified() }.measuredWidth
    override val intrinsicHeight: Int
        get() = createImpatientlyBoundView().apply { measureUnspecified() }.measuredHeight

    override fun createVB(): VB = inflate(LayoutInflater.from(context))
    override fun createImpatientlyBoundView(): View = createVB().also { bindImpatiently(it) }.root

    @Suppress("UNCHECKED_CAST")
    private fun _bind(vb: ViewBinding, _lifecycle: LifecycleOwner) {
        return try {
            bind(d, vb as VB, _lifecycle)
        } catch (e: android.util.AndroidRuntimeException) { // maybe mainThread is required
            Completable.fromAction { bind(d, vb as VB, _lifecycle) }
                .subscribeOn(AndroidSchedulers.mainThread())
                .blockingAwait()
        }
    }

    override fun bind(vb: ViewBinding, lifecycle: LifecycleOwner) {
        _bind(vb, lifecycle)
    }

    override fun bindImpatiently(vb: ViewBinding) {
        val _lifecycle = ExposedLifecycleOwner().apply { emitResume() }
        _bind(vb, _lifecycle)
        _lifecycle.emitDestroy()
    }
}