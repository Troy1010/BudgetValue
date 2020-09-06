package com.example.budgetvalue.layers.z_ui.TMTableView

import android.view.View
import com.example.tmcommonkotlin.logz

object Data2dConverter {
    fun <V:View,D:Any,V2:View,D2:Any>convertByColumnDataToCellData(data2d:List<TableViewColumnData<V,D,V2,D2>>): ArrayList<ArrayList<TableViewCellData>> {
        val rowCount = (data2d.map{it.data.size}.max()?:0)+1 // +1 for header
        val returning = ArrayList<ArrayList<TableViewCellData>>()
        for(yPos in 0 until rowCount) {
            returning.add(ArrayList())
            for (columnData in data2d) {
                if (yPos==0) {
                    returning[yPos].add(TableViewCellData(
                        columnData.headerViewFactory,
                        columnData.headerBindAction as (View, Any)->Unit,
                        columnData.header
                    ))
                } else {
                    returning[yPos].add(TableViewCellData(
                        columnData.cellViewFactory,
                        columnData.cellBindAction as (View, Any)->Unit,
                        columnData.data.getOrNull(yPos-1)?:"" // -1 for header
                    ))
                }
            }
        }
        return returning
    }
}