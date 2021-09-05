package com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.extensions.easyText
import com.tminus1010.budgetvalue._core.extensions.lifecycleOwner
import com.tminus1010.budgetvalue._core.middleware.ui.ExposedLifecycleOwner
import com.tminus1010.budgetvalue._core.middleware.ui.onDone
import com.tminus1010.budgetvalue.databinding.ItemMoneyEditTextBinding
import com.tminus1010.budgetvalue.databinding.ItemTextViewBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.measureUnspecified
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

data class ViewItemRecipe3__<VB : ViewBinding> constructor(
    private val context: Context,
    private val inflate: (LayoutInflater) -> VB,
    private val _bind: (VB) -> Unit,
) : IViewItemRecipe3 {
    override val intrinsicWidth: Int
        get() = createImpatientlyBoundView().apply { measureUnspecified() }.measuredWidth
    override val intrinsicHeight: Int
        get() = createImpatientlyBoundView().apply { measureUnspecified() }.measuredHeight

    override fun createVB(): VB =
        inflate(LayoutInflater.from(context))
    override fun createImpatientlyBoundView(): View =
        createVB().also { bindImpatiently(it) }.root

    @Suppress("UNCHECKED_CAST")
    override fun bind(vb: ViewBinding) {
        return try {
            _bind(vb as VB)
        } catch (e: android.util.AndroidRuntimeException) { // maybe mainThread is required
            Completable.fromAction { bind(vb as VB) }
                .subscribeOn(AndroidSchedulers.mainThread())
                .blockingAwait()
        }
    }

    override fun bindImpatiently(vb: ViewBinding) {
        val _lifecycle = ExposedLifecycleOwner().apply { emitResume() }
        vb.root.lifecycleOwner = _lifecycle
        bind(vb)
        _lifecycle.emitDestroy()
    }
}