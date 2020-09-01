package com.example.budgetvalue.layers.z_ui.misc

import android.app.Activity
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.R

class DecorationForRecyclerView(activity: Activity, val orientation:Int = VERTICAL) : RecyclerView.ItemDecoration() {
    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
    }
    val mDivider = ContextCompat.getDrawable(activity, R.drawable.divider)!!
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        canvas.save()
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)

            val params =
                child.layoutParams as RecyclerView.LayoutParams

            when (orientation) {
                HORIZONTAL -> {
                    val right = child.right + params.rightMargin
                    val left = right - mDivider.intrinsicWidth
                    mDivider.setBounds(left, 0, right, parent.height)
                }
                else -> {
                    val bottom = child.bottom + params.bottomMargin
                    val top = bottom - mDivider.intrinsicHeight
                    mDivider.setBounds(0, top, parent.width, bottom)
                }
            }
            mDivider.draw(canvas)
        }
        canvas.restore()
    }
}