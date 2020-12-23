package com.example.budgetvalue.layer_ui.TMTableView2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.R
import com.example.budgetvalue.layer_ui.TMTableView.Decoration
import com.example.budgetvalue.layer_ui.TMTableView.IViewItemRecipe

class FrozenRowDecoration(context: Context, orientation: Int, val colFreezeCount: Int, val recipes2D: List<List<IViewItemRecipe>>) : Decoration(context, orientation) {
    val defaultDividerDrawable by lazy { ContextCompat.getDrawable(context, R.drawable.divider)!! }
    val defaultDividerHeight by lazy { defaultDividerDrawable.intrinsicHeight }
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
        if (colFreezeCount>1) TODO()
        if (colFreezeCount==1) {
            if (orientation== TableViewDecorationTier1.VERTICAL) TODO()
            val child = parent
            val layoutParams = child.layoutParams as ConstraintLayout.LayoutParams
            val view = recipes2D[0][0].createBoundView()

            val top = child.top - layoutParams.topMargin
            val rect = Rect(0, top, firstColWidth.value, top + recipes2D[0][0].intrinsicHeight)

            val widthSpec = View.MeasureSpec.makeMeasureSpec(rect.width(), View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(rect.height(), View.MeasureSpec.EXACTLY)
            view.measure(widthSpec, heightSpec)
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)

            canvas.save()
            canvas.translate(rect.left.toFloat(), rect.top.toFloat())
            view.draw(canvas)
            canvas.restore()

            // ## vertical divider
            defaultDividerDrawable.setBounds(firstColWidth.value, top, firstColWidth.value+defaultDividerHeight, top + recipes2D[0][0].intrinsicHeight)
            defaultDividerDrawable.draw(canvas)
        }
    }
}