package com.example.budgetvalue.layers.z_ui.misc

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.layers.z_ui.views.MyTableView
import com.example.budgetvalue.util.exactWidth
import com.example.budgetvalue.util.intrinsicWidth2
import com.example.tmcommonkotlin.logz

class MyTableViewLayoutManager(
    context: Context,
    val adapter: MyTableViewDataRecyclerViewAdapter,
    val myTableView: MyTableView
) : LinearLayoutManager(context, RecyclerView.VERTICAL, false) {
    var firstPass = true
    override fun isAutoMeasureEnabled()=false
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        if (firstPass) {
            firstPass = false
            myTableView.requestLayout()
            myTableView.initColumnWidths(
                adapter,
                myTableView.generateIntrinsicWidths(adapter, myTableView.dataZ, myTableView.columnCount),
                myTableView.columnCount,
                myTableView.measuredWidth
            )
        }
        super.onLayoutChildren(recycler, state)
    }
}