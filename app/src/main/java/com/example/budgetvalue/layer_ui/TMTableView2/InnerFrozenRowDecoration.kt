package com.example.budgetvalue.layer_ui.TMTableView2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.Orientation
import com.example.budgetvalue.R
import com.example.budgetvalue.layer_ui.TMTableView.Decoration

class InnerFrozenRowDecoration(
    context: Context,
    orientation: Orientation,
    val recipeGrid: RecipeGrid,
    val rowFreezeCount: Int
) : Decoration(context, orientation) {
    val defaultDividerDrawable by lazy { ContextCompat.getDrawable(context, R.drawable.divider)!! }
    val defaultDividerHeight by lazy { defaultDividerDrawable.intrinsicHeight }
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        // * Horizontal dividers are drawn by parent
        super.onDrawOver(canvas, parent, state)
        if (rowFreezeCount>1) TODO()
        if (rowFreezeCount==1) {
            if (orientation == Orientation.VERTICAL) TODO()
            val child = parent
            val layoutParams = child.layoutParams as ConstraintLayout.LayoutParams
            val view = recipeGrid[0][0].createBoundView()
            val width = recipeGrid.getColumnWidth(0)
            val height = recipeGrid.getRowHeight(0)

            val top = child.top - layoutParams.topMargin
            val rect = Rect(0, top, width, top + height)

            val widthSpec = View.MeasureSpec.makeMeasureSpec(rect.width(), View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(rect.height(), View.MeasureSpec.EXACTLY)
            view.measure(widthSpec, heightSpec)
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)

            canvas.save()
            canvas.translate(rect.left.toFloat(), rect.top.toFloat())
            view.draw(canvas)
            canvas.restore()

            // ## Vertical Divider
            defaultDividerDrawable.setBounds(width, top, width + defaultDividerHeight, top + height)
            defaultDividerDrawable.draw(canvas)
        }
    }
}