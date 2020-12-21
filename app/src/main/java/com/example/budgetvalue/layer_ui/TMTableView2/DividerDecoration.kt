package com.example.budgetvalue.layer_ui.TMTableView2

import android.content.Context
import android.graphics.Canvas
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.R
import com.example.budgetvalue.intrinsicHeight2
import com.example.budgetvalue.intrinsicWidth2
import com.example.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import com.example.budgetvalue.measureUnspecified
import com.tminus1010.tmcommonkotlin.logz.logz

class DividerDecoration(
    context: Context, val orientation: Int = VERTICAL, val separatorMap: Map<Int, IViewItemRecipe>
) : RecyclerView.ItemDecoration() {
    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
    }

    val defaultDividerDrawable = ContextCompat.getDrawable(context, R.drawable.divider)!!
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        canvas.save()
        // ! Assuming that the last row does not need a separator below it.
        for ((i, child) in parent.children.withIndex().toList().dropLast(1)) {
            val params = child.layoutParams as RecyclerView.LayoutParams

            if (i in separatorMap.keys) {
                val view = separatorMap[i]!!.viewProvider()
                view.measureUnspecified()
//                val top = child.top + params.topMargin
//                logz("top:$top")
//                val bottom = top + view.intrinsicHeight2
//                val bottom = top + 80
                view.layout(0, 300, parent.width, 800)
                view.draw(canvas)
            } else {
                when (orientation) {
                    HORIZONTAL -> {
                        TODO("HORIZONTAL is not yet supported")
//                        val left = child.right + params.rightMargin
//                        val right = left + defaultDividerDrawable.intrinsicWidth
//                        defaultDividerDrawable.setBounds(left, 0, right, parent.height)
                    }
                    else -> {
                        val top = child.bottom + params.bottomMargin
                        val bottom = top + defaultDividerDrawable.intrinsicHeight
                        defaultDividerDrawable.setBounds(0, top, parent.width, bottom)
                    }
                }
                defaultDividerDrawable.draw(canvas)
            }
        }
        canvas.restore()
    }
}