package com.tminus1010.budgetvalue.all_features.framework.view.tmTableView3

import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.tminus1010.budgetvalue.all_features.framework.view.ExposedLifecycleOwner
import com.tminus1010.tmcommonkotlin.misc.extensions.lifecycleOwner
import com.tminus1010.tmcommonkotlin.misc.extensions.measureUnspecified
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable

data class ViewItemRecipe3<VB : ViewBinding, D : Any?>(
    private val vbLambda: () -> VB,
    private val bindAction: (D, VB, LifecycleOwner) -> Unit,
    private val data: D? = null,
) : IViewItemRecipe3 {
    // This cast allows IViewItemRecipe of different view types to be stored together
    @Suppress("UNCHECKED_CAST")
    private val bindAction_ = bindAction as (Any?, ViewBinding, LifecycleOwner) -> Unit
    override val intrinsicWidth: Int
        get() = createImpatientlyBoundView().apply { measureUnspecified() }.measuredWidth
    override val intrinsicHeight: Int
        get() = createImpatientlyBoundView().apply { measureUnspecified() }.measuredHeight

    override fun createVB(): ViewBinding = vbLambda()
    override fun createVB(viewGroup: ViewGroup?): ViewBinding {
        TODO("Not yet implemented")
    }

    override fun createImpatientlyBoundView(): View = createVB().also { bindImpatiently(it) }.root
    override fun intrinsicHeight(width: Int): Int {
        return createImpatientlyBoundView()
            .apply {
                measure(
                    View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                )
            }
            .measuredHeight
    }


    private fun _bind(vb: ViewBinding, _lifecycle: LifecycleOwner) {
        return try {
            bindAction_(data, vb, _lifecycle)
        } catch (e: android.util.AndroidRuntimeException) { // maybe mainThread is required
            Completable.fromAction { bindAction_(data, vb, _lifecycle) }
                .subscribeOn(AndroidSchedulers.mainThread())
                .blockingAwait()
        }
    }

    override fun bind(vb: ViewBinding) {
        _bind(vb, vb.root.lifecycleOwner!!)
    }

    fun bindImpatiently(vb: ViewBinding) {
        val lifecycleRedef = ExposedLifecycleOwner().apply { emitResume() }
        vb.root.lifecycleOwner = lifecycleRedef
        _bind(vb, lifecycleRedef)
        lifecycleRedef.emitDestroy()
    }
}