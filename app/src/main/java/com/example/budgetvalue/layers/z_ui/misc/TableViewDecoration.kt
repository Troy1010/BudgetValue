package com.example.budgetvalue.layers.z_ui.misc

import android.app.Activity
import android.graphics.Canvas
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.R
import com.example.tmcommonkotlin.logz

class TableViewDecoration(activity: Activity, val orientation:Int = VERTICAL, val bHasSubItems:Boolean=false) : RecyclerView.ItemDecoration() {
    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
    }
    val mDivider = ContextCompat.getDrawable(activity, R.drawable.divider)!!
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        canvas.save()
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
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
        // in between items
        if (bHasSubItems) {
//            logz("hasSubItems")
            val lastRow = (parent.getChildAt(parent.childCount-1) as LinearLayout)
            for (subChild in lastRow.children) {
                if (subChild == lastRow.children.last())
                    break
//                logz("subChild..${(subChild as TextView).text}") // somehow this makes the for loop actually loop over all items
                when(orientation) {
                    HORIZONTAL -> {
                        val bottom = subChild.bottom
                        val top = bottom - mDivider.intrinsicHeight
                        mDivider.setBounds(0, top, lastRow.right, bottom)
                    }
                    else -> {
                        val right = subChild.right
                        val left = right - mDivider.intrinsicWidth
                        mDivider.setBounds(left, 0, right, lastRow.bottom)
//                        logz("mDivider bounds: ${mDivider}")
                    }
                }
                mDivider.draw(canvas)
            }
        }
        canvas.restore()
    }
}