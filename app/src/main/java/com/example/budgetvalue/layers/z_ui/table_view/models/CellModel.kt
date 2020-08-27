package com.example.budgetvalue.layers.z_ui.table_view.models

import com.evrencoskun.tableview.sort.ISortableModel

class CellModel (val data: Any, private val mId: String="0") : ISortableModel {

    override fun getId(): String {
        return mId
    }

    override fun getContent(): Any? {
        return data
    }

}