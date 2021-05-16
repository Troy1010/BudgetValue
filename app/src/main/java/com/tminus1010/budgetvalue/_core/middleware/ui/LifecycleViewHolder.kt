package com.tminus1010.budgetvalue._core.middleware.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class LifecycleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    var lifecycle: ExposedLifecycleOwner? = null

    fun onAttached() {
        if (lifecycle != null) error("Shouldn't this already been removed..?")
        lifecycle = ExposedLifecycleOwner()
            .apply { emitResume() }
    }

    fun onDetached() {
        lifecycle!!.emitDestroy()
        lifecycle = null
    }
}