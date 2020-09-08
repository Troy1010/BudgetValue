package com.example.budgetvalue.layers.z_ui.TMTableView

import android.content.Context
import android.graphics.Canvas
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.iterator
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.R

class Decoration(context: Context, val orientation:Int = VERTICAL, val bHasSubItems:Boolean=false) : RecyclerView.ItemDecoration() {
    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
    }
    val mDivider = ContextCompat.getDrawable(context, R.drawable.divider)!!
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        canvas.save()
        var bFirst = true
        for (child in parent.children.toList()) {
            if (bFirst) {
                bFirst = false
                continue
            }
            val params = child.layoutParams as RecyclerView.LayoutParams

            when (orientation) {
                HORIZONTAL -> {
                    val left = child.left + params.leftMargin
                    val right = left + mDivider.intrinsicWidth
                    mDivider.setBounds(left, 0, right, parent.height)
                }
                else -> {
                    val top = child.top + params.topMargin
                    val bottom = top + mDivider.intrinsicHeight
                    mDivider.setBounds(0, top, parent.width, bottom)
                }
            }
            mDivider.draw(canvas)
        }
        canvas.restore()
    }
}