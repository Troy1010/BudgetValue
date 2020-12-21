package com.example.budgetvalue.layer_ui.TMTableView2

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import android.view.View.MeasureSpec
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.layer_ui.TMTableView.IViewItemRecipe

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

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        val pos = parent.getChildAdapterPosition(view)
        when (pos) {
            in separatorMap.keys -> outRect.apply { bottom = separatorMap[pos]!!.intrinsicHeight }
            else -> outRect.apply { bottom = dividerHeightDefault }
        }
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        // ! Assuming that the last item does not need a separator after it.
        for (child in parent.children.toList().dropLast(1)) {
            val i = parent.getChildAdapterPosition(child)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams

            if (i in separatorMap.keys) {
                val view = separatorMap[i]!!.createBoundView()

                val top = child.bottom + layoutParams.bottomMargin
                val rect = Rect(0, top, parent.width, top + separatorMap[i]!!.intrinsicHeight)

                val widthSpec = MeasureSpec.makeMeasureSpec(rect.width(), MeasureSpec.EXACTLY)
                val heightSpec = MeasureSpec.makeMeasureSpec(rect.height(), MeasureSpec.EXACTLY)
                view.measure(widthSpec, heightSpec)
                view.layout(0, 0, view.measuredWidth, view.measuredHeight)

                canvas.save()
                canvas.translate(rect.left.toFloat(), rect.top.toFloat())
                view.draw(canvas)
                canvas.restore()
            } else {
                when (orientation) {
                    HORIZONTAL -> {
                        TODO("HORIZONTAL is not yet tested")
//                        val left = child.right + params.rightMargin
//                        Rect(left, 0, left + dividerHeightDefault, parent.height)
                    }
                    else -> {
                        val top = child.bottom + layoutParams.bottomMargin
                        Rect(0, top, parent.width, top + dividerHeightDefault)
                    }
                }.also { canvas.drawRect(it, dividerPaintDefault) }
            }
        }
    }
}