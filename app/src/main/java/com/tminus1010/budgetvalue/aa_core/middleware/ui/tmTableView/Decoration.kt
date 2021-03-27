package com.tminus1010.budgetvalue.aa_core.middleware.ui.tmTableView

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.budgetvalue.aa_core.middleware.Orientation
import com.tminus1010.budgetvalue.R

open class Decoration(
    context: Context,
    val orientation: Orientation = Orientation.VERTICAL,
) : RecyclerView.ItemDecoration() {
    val dividerDrawable = ContextCompat.getDrawable(context, R.drawable.divider)!!
    // TODO("currently, no extra spacing is provided, because the default divider is so small it doesn't seem to matter atm")
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        canvas.save()
        for (child in parent.children.drop(1)) {
            val params = child.layoutParams as RecyclerView.LayoutParams

            when (orientation) {
                Orientation.HORIZONTAL -> {
                    val left = child.left + params.leftMargin
                    val right = left + dividerDrawable.intrinsicWidth
                    dividerDrawable.setBounds(left, 0, right, parent.height)
                }
                Orientation.VERTICAL -> {
                    val top = child.top + params.topMargin
                    val bottom = top + dividerDrawable.intrinsicHeight
                    dividerDrawable.setBounds(0, top, parent.width, bottom)
                }
            }
            dividerDrawable.draw(canvas)
        }
        canvas.restore()
    }
}