package com.example.budgetvalue.extensions

import android.view.View
import androidx.recyclerview.widget.RecyclerView

val RecyclerView.LayoutManager.children : Iterable<View>
        get() {
            val returning = ArrayList<View>()
            for (i in 0 until this.childCount) {
                this.getChildAt(i)?.also { returning.add(it) }
            }
            return returning
        }