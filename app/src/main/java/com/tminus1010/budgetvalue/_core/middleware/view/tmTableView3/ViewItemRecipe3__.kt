package com.tminus1010.budgetvalue._core.middleware.view.tmTableView3

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import com.tminus1010.budgetvalue._core.all.extensions.lifecycleOwner
import com.tminus1010.budgetvalue._core.middleware.view.ExposedLifecycleOwner
import com.tminus1010.tmcommonkotlin.misc.extensions.measureUnspecified
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable

data class ViewItemRecipe3__<VB : ViewBinding> constructor(
    private val context: Context,
    private val inflate: (LayoutInflater) -> VB,
    private val styler: ((VB) -> Unit)? = null,
    private val _bind: ((VB) -> Unit)? = null,
) : IViewItemRecipe3 {
    override val intrinsicWidth: Int
        get() = createImpatientlyBoundView().apply { measureUnspecified() }.measuredWidth
    override val intrinsicHeight: Int
        get() = createImpatientlyBoundView().apply { measureUnspecified() }.measuredHeight

    /**
     * This intrinsicHeight depends on a specified width.
     *
     * Useful for TextViews which will have more height if the text does not fit in the width.
     */
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

    override fun createVB(): VB =
        inflate(LayoutInflater.from(context))
            .also { styler?.also { styler -> styler(it) } }

    override fun createImpatientlyBoundView(): View =
        createVB().also { bindImpatiently(it) }.root

    @Suppress("UNCHECKED_CAST")
    override fun bind(vb: ViewBinding) {
        try {
            _bind?.also { it(vb as VB) }
        } catch (e: android.util.AndroidRuntimeException) { // maybe mainThread is required
            Completable.fromAction { _bind?.also { it(vb as VB) } }
                .subscribeOn(AndroidSchedulers.mainThread())
                .blockingAwait()
        }
    }

    fun bindImpatiently(vb: ViewBinding) {
        try {
            val _lifecycle = ExposedLifecycleOwner().apply { emitResume() }
            vb.root.lifecycleOwner = _lifecycle
            bind(vb)
            _lifecycle.emitDestroy()
        } catch (e: java.lang.IllegalStateException) { // maybe mainThread is required
            Completable.fromAction {
                val _lifecycle = ExposedLifecycleOwner().apply { emitResume() }
                vb.root.lifecycleOwner = _lifecycle
                bind(vb)
                _lifecycle.emitDestroy()
            }.subscribeOn(AndroidSchedulers.mainThread())
                .blockingAwait()
        }
    }
}