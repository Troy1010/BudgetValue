package com.example.budgetvalue.layers.z_ui.misc

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.layers.z_ui.TMTableView.TMTableView

class MyTableViewLayoutManager2(
    val TMTableView: TMTableView,
    orientationz: Int = RecyclerView.VERTICAL
) : LinearLayoutManager(TMTableView.context, orientationz, false) {
    var firstPass = true
    override fun isAutoMeasureEnabled()=true

    override fun onMeasure(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        widthSpec: Int,
        heightSpec: Int
    ) {
//        logz("widthSpec:${widthSpec} heightSpec:$heightSpec")
        super.onMeasure(recycler, state, 0, heightSpec)
    }

//    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
//        if (firstPass && (myTableView.measuredWidth != 0)) {
//            firstPass = false
//            myTableView.requestLayout()
//            myTableView.columnWidths = myTableView.generateColumnWidths(
//                myTableView.generateMinWidths(myTableView.headerFactory, myTableView.headerBindAction, myTableView.headers),
//                myTableView.generateIntrinsicWidths(myTableView.rowFactory, myTableView.cellBindAction, myTableView.dataZ, myTableView.columnCount),
//                myTableView.measuredWidth
//            )
//        }
//        super.onLayoutChildren(recycler, state)
//    }
}