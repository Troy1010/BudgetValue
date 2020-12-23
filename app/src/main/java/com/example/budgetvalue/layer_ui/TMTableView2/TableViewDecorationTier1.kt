package com.example.budgetvalue.layer_ui.TMTableView2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import android.view.View.MeasureSpec
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.R
import com.example.budgetvalue.layer_ui.TMTableView.IViewItemRecipe

class TableViewDecorationTier1(
    val context: Context,
    val orientation: Int = VERTICAL,
    val separatorMap: Map<Int, IViewItemRecipe>,
    val recipes2D: List<List<IViewItemRecipe>>,
    val colFreezeCount: Int = 0
) : RecyclerView.ItemDecoration() {
    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
    }
    val defaultDividerDrawable by lazy { ContextCompat.getDrawable(context, R.drawable.divider)!! }
    val defaultDividerHeight by lazy { defaultDividerDrawable.intrinsicHeight }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        val pos = parent.getChildAdapterPosition(view)
        when (pos) {
            in separatorMap.keys -> outRect.apply { bottom = separatorMap[pos]!!.intrinsicHeight }
            else -> outRect.apply { bottom = defaultDividerHeight }
        }
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        // # Dividers
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
                    HORIZONTAL -> TODO("HORIZONTAL is not yet implemented")
                    else -> {
                        val top = child.bottom + layoutParams.bottomMargin
                        defaultDividerDrawable.setBounds(0, top, parent.width, top + defaultDividerHeight)
                    }
                }
                defaultDividerDrawable.draw(canvas)
            }
        }
        // # Frozen Columns
        if (colFreezeCount>1) TODO()
        if (colFreezeCount==1) {
            if (orientation== HORIZONTAL) TODO()
            for (child in parent.children) {
                val i = parent.getChildAdapterPosition(child)
                val layoutParams = child.layoutParams as RecyclerView.LayoutParams
                val view = recipes2D[i][0].createBoundView()

                val top = child.top - layoutParams.topMargin
                val rect = Rect(0, top, firstColWidth.value, top + recipes2D[i][0].intrinsicHeight)

                val widthSpec = MeasureSpec.makeMeasureSpec(rect.width(), MeasureSpec.EXACTLY)
                val heightSpec = MeasureSpec.makeMeasureSpec(rect.height(), MeasureSpec.EXACTLY)
                view.measure(widthSpec, heightSpec)
                view.layout(0, 0, view.measuredWidth, view.measuredHeight)

                canvas.save()
                canvas.translate(rect.left.toFloat(), rect.top.toFloat())
                view.draw(canvas)
                canvas.restore()

                // TODO("3 should not be hard coded")
                defaultDividerDrawable.setBounds(firstColWidth.value, top, firstColWidth.value+3, top + recipes2D[i][0].intrinsicHeight)
                defaultDividerDrawable.draw(canvas)
            }
        }
    }
}