package com.example.budgetvalue.layer_ui.TMTableView

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.R

open class Decoration(
    context: Context, val orientation: Int = VERTICAL,
) : RecyclerView.ItemDecoration() {
    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
    }

    val dividerDrawable = ContextCompat.getDrawable(context, R.drawable.divider)!!
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        canvas.save()
        for (child in parent.children.drop(1)) {
            val params = child.layoutParams as RecyclerView.LayoutParams

            when (orientation) {
                HORIZONTAL -> {
                    val left = child.left + params.leftMargin
                    val right = left + dividerDrawable.intrinsicWidth
                    dividerDrawable.setBounds(left, 0, right, parent.height)
                }
                else -> {
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