package com.tminus1010.budgetvalue.middleware.ui.TMTableView2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import android.view.View.MeasureSpec
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.budgetvalue.middleware.Orientation
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.middleware.ui.TMTableView.IViewItemRecipe

class OuterDecoration(
    val context: Context,
    val orientation: Orientation = Orientation.VERTICAL,
    val dividerMap: Map<Int, IViewItemRecipe>,
    val recipeGrid: RecipeGrid,
    val colFreezeCount: Int = 0,
    val rowFreezeCount: Int = 0,
) : RecyclerView.ItemDecoration() {
    val defaultDividerDrawable by lazy { ContextCompat.getDrawable(context, R.drawable.divider)!! }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        if (orientation == Orientation.HORIZONTAL) TODO()
        when (val j = parent.getChildAdapterPosition(view) + rowFreezeCount) {
            in dividerMap.keys -> outRect.apply { top = dividerMap[j]!!.intrinsicHeight }
            0 -> return // The first item does not implicitly get a divider above it.
            else -> outRect.apply { top = defaultDividerDrawable.intrinsicHeight }
        }
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        // # Horizontal Dividers
        for (child in parent.children) {
            val j = parent.getChildAdapterPosition(child) + rowFreezeCount
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams

            if (j in dividerMap.keys) {
                val view = dividerMap[j]!!.createBoundView()

                val top = child.top - layoutParams.topMargin - dividerMap[j]!!.intrinsicHeight
                val rect = Rect(0, top, parent.width, top + dividerMap[j]!!.intrinsicHeight)

                val widthSpec = MeasureSpec.makeMeasureSpec(rect.width(), MeasureSpec.EXACTLY)
                val heightSpec = MeasureSpec.makeMeasureSpec(rect.height(), MeasureSpec.EXACTLY)
                view.measure(widthSpec, heightSpec)
                view.layout(0, 0, view.measuredWidth, view.measuredHeight)

                canvas.save()
                canvas.translate(rect.left.toFloat(), rect.top.toFloat())
                view.draw(canvas)
                canvas.restore()
            } else {
                if (j == 0) continue // The first item does not implicitly get a divider above it.
                when (orientation) {
                    Orientation.HORIZONTAL -> TODO()
                    Orientation.VERTICAL -> {
                        val top =
                            child.top - layoutParams.topMargin - defaultDividerDrawable.intrinsicHeight
                        defaultDividerDrawable.setBounds(0,
                            top,
                            parent.width,
                            top + defaultDividerDrawable.intrinsicHeight)
                    }
                }
                defaultDividerDrawable.draw(canvas)
            }
        }
        // # Frozen Columns
        if (colFreezeCount > 1) TODO()
        if (colFreezeCount == 1) {
            if (orientation == Orientation.HORIZONTAL) TODO()
            val width = recipeGrid.getColumnWidth(0)
            for (child in parent.children) {
                val j = parent.getChildAdapterPosition(child) + rowFreezeCount
                val layoutParams = child.layoutParams as RecyclerView.LayoutParams
                val view = recipeGrid[j][0].createBoundView()
                val height = recipeGrid.getRowHeight(j)

                val top = child.top - layoutParams.topMargin
                val rect = Rect(0, top, width, top + height)

                val widthSpec = MeasureSpec.makeMeasureSpec(rect.width(), MeasureSpec.EXACTLY)
                val heightSpec = MeasureSpec.makeMeasureSpec(rect.height(), MeasureSpec.EXACTLY)
                view.measure(widthSpec, heightSpec)
                view.layout(0, 0, view.measuredWidth, view.measuredHeight)

                canvas.save()
                canvas.translate(rect.left.toFloat(), rect.top.toFloat())
                view.draw(canvas)
                canvas.restore()

                // ## vertical divider
                defaultDividerDrawable.setBounds(width,
                    top,
                    width + defaultDividerDrawable.intrinsicHeight,
                    top + height)
                defaultDividerDrawable.draw(canvas)
            }
        }
    }
}