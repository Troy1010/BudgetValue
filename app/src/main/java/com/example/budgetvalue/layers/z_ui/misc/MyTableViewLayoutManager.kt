package com.example.budgetvalue.layers.z_ui.misc

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.layers.z_ui.views.MyTableView

class MyTableViewLayoutManager(
    val myTableView: MyTableView,
    orientationz: Int = RecyclerView.VERTICAL
) : LinearLayoutManager(myTableView.context, orientationz, false) {
    var firstPass = true
    override fun isAutoMeasureEnabled()=false
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
//        if (firstPass) {
//            firstPass = false
//            myTableView.requestLayout()
//            myTableView.columnWidths = myTableView.generateColumnWidths(
//                myTableView.generateMinWidths(myTableView.headerFactory, myTableView.headerBindAction, myTableView.headers),
//                myTableView.generateIntrinsicWidths(myTableView.rowFactory, myTableView.cellBindAction, myTableView.dataZ, myTableView.columnCount),
//                myTableView.measuredWidth
//            )
//        }
        super.onLayoutChildren(recycler, state)
    }
}