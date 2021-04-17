package com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.tminus1010.budgetvalue._core.middleware.measureUnspecified
import com.tminus1010.budgetvalue._core.middleware.ui.ExposedLifecycleOwner
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable

data class ViewItemRecipe3<VB : ViewBinding, D : Any?>(
    private val vbLambda: () -> VB,
    private val data: D,
    private val bindAction: (D, VB, LifecycleOwner) -> Unit,
) : IViewItemRecipe3 {
    // This cast allows IViewItemRecipe of different view types to be stored together
    @Suppress("UNCHECKED_CAST")
    private val bindAction_ = bindAction as (Any?, ViewBinding, LifecycleOwner) -> Unit
    override val intrinsicWidth: Int
        get() = createImpatientlyBoundView().apply { measureUnspecified() }.measuredWidth
    override val intrinsicHeight: Int
        get() = createImpatientlyBoundView().apply { measureUnspecified() }.measuredHeight

    override fun createVB(): ViewBinding = vbLambda()
    override fun createImpatientlyBoundView(): View = createVB().also { bindImpatiently(it) }.root


    private fun _bind(vb: ViewBinding, _lifecycle: LifecycleOwner) {
        return try {
            bindAction_(data, vb, _lifecycle)
        } catch (e: android.util.AndroidRuntimeException) { // maybe mainThread is required
            Completable.fromAction { bindAction_(data, vb, _lifecycle) }
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