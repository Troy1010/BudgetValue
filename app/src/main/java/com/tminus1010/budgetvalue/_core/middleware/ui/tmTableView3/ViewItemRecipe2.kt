package com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3

import android.view.View
import androidx.viewbinding.ViewBinding
import com.tminus1010.budgetvalue._core.middleware.measureUnspecified
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable

data class ViewItemRecipe2<VB : ViewBinding, D : Any?>(
    private val vbLambda: () -> VB,
    private val data: D,
    private val bindAction: (VB, D) -> Unit,
) : IViewItemRecipe2 {
    // This cast allows IViewItemRecipe of different view types to be stored together
    @Suppress("UNCHECKED_CAST")
    private val bindAction_ = bindAction as (ViewBinding, Any?) -> Unit
    override val intrinsicWidth
        get() = createBoundView().apply { measureUnspecified() }.measuredWidth
    override val intrinsicHeight
        get() = createBoundView().apply { measureUnspecified() }.measuredHeight

    override fun createView(): ViewBinding = vbLambda()
    override fun createBoundView(): View = createView().also { bind(it) }.root

    override fun bind(vb: ViewBinding) {
        try {
            bindAction_(vb, data)
        } catch (e: android.util.AndroidRuntimeException) { // maybe mainThread is required
            Completable.fromAction { bindAction_(vb, data) }
                .subscribeOn(AndroidSchedulers.mainThread())
                .blockingAwait()
        }
    }
}