package com.tminus1010.buva.framework.android

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.tmcommonkotlin.view.extensions.toPX

class MarginDecoration(
    val dp: Int,
    val spanCount: Int? = null,
) : RecyclerView.ItemDecoration() {
    var spacing: Int = -1
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        if (spacing == -1) spacing = dp.toPX(parent.context)
        when (val layoutManager = parent.layoutManager) {
            is LinearLayoutManager ->
                if (layoutManager.orientation == LinearLayoutManager.VERTICAL)
                    when (parent.getChildAdapterPosition(view)) {
                        0 -> return // The first item does not get a margin
                        else ->
                            if (layoutManager.reverseLayout)
                                outRect.apply { bottom = spacing }
                            else
                                outRect.apply { top = spacing }
                    }
                else
                    TODO()
            is GridLayoutManager ->
                if (layoutManager.orientation == LinearLayoutManager.VERTICAL)
                    TODO()
                else
                    TODO()
            else -> TODO()
        }
    }
}