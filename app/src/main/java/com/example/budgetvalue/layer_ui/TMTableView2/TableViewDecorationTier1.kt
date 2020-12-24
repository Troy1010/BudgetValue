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
import com.tminus1010.tmcommonkotlin.logz.logz

class TableViewDecorationTier1(
    val context: Context,
    val orientation: Int = VERTICAL,
    val separatorMap: Map<Int, IViewItemRecipe>,
    val recipeGrid: RecipeGrid,
    val rowFreezeCount: Int = 0,
    val colFreezeCount: Int = 0,
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
        val j = parent.getChildAdapterPosition(view) + rowFreezeCount
        when (j) {
            in separatorMap.keys -> outRect.apply { top = separatorMap[j]!!.intrinsicHeight }
            else -> {
                if (j==0) return // The first item does not implicitly get a divider above it.
                outRect.apply { top = defaultDividerHeight }
            }
        }
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        // # Dividers
        for (child in parent.children) {
            val j = parent.getChildAdapterPosition(child) + rowFreezeCount
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams

            if (j in separatorMap.keys) {
                val view = separatorMap[j]!!.createBoundView()

                val top = child.top - layoutParams.topMargin - separatorMap[j]!!.intrinsicHeight
                val rect = Rect(0, top, parent.width, top + separatorMap[j]!!.intrinsicHeight)

                val widthSpec = MeasureSpec.makeMeasureSpec(rect.width(), MeasureSpec.EXACTLY)
                val heightSpec = MeasureSpec.makeMeasureSpec(rect.height(), MeasureSpec.EXACTLY)
                view.measure(widthSpec, heightSpec)
                view.layout(0, 0, view.measuredWidth, view.measuredHeight)

                canvas.save()
                canvas.translate(rect.left.toFloat(), rect.top.toFloat())
                view.draw(canvas)
                canvas.restore()
            } else {
                if (j==0) continue // The first item does not implicitly get a divider above it.
                when (orientation) {
                    HORIZONTAL -> TODO()
                    else -> {
                        val top = child.top - layoutParams.topMargin - defaultDividerHeight
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
                defaultDividerDrawable.setBounds(width, top, width+defaultDividerHeight, top + height)
                defaultDividerDrawable.draw(canvas)
            }
        }
    }
}