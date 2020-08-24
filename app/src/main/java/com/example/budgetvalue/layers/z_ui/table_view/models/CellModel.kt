package com.example.budgetvalue.layers.z_ui.table_view.models

import com.evrencoskun.tableview.sort.ISortableModel

class CellModel (private val mId: String, val data: Any) : ISortableModel {

    override fun getId(): String {
        return mId
    }

    override fun getContent(): Any? {
        return data
    }

}