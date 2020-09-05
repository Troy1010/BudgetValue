package com.example.budgetvalue.layers.z_ui.misc

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.layers.z_ui.TMTableView.TMTableView

class MyTableViewLayoutManager(
    context: Context
) : LinearLayoutManager(context, RecyclerView.VERTICAL, false) {
    override fun isAutoMeasureEnabled()=false
}