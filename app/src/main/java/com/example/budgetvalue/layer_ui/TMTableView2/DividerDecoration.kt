package com.example.budgetvalue.layer_ui.TMTableView2

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import com.example.budgetvalue.measureUnspecified

class DividerDecoration(
    val separatorMap: Map<Int, IViewItemRecipe>,
    val orientation: Int = VERTICAL,
) : RecyclerView.ItemDecoration() {
    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
    }

    val dividerHeightDefault = 1
    val dividerPaintDefault by lazy {
        Paint().apply { style = Paint.Style.FILL; color = Color.parseColor("#ffffff") }
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        canvas.save()
        // ! Assuming that the last row does not need a separator after it.
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
                        TODO("HORIZONTAL is not yet tested")
//                        val left = child.right + params.rightMargin
//                        val right = left + defaultDividerDrawable.intrinsicWidth
//                        Rect(left, 0, right, parent.height)
                    }
                    else -> {
                        val top = child.bottom + params.bottomMargin
                        Rect(0, top, parent.width, top + dividerHeightDefault)
                    }
                }.also { canvas.drawRect(it, dividerPaintDefault) }
            }
        }
        canvas.restore()
    }
}