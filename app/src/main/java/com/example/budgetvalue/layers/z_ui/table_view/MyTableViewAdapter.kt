package com.example.budgetvalue.layers.z_ui.table_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.example.budgetvalue.R
import com.example.budgetvalue.layers.z_ui.table_view.models.CellModel
import com.example.budgetvalue.layers.z_ui.table_view.models.ColumnHeaderModel
import com.example.budgetvalue.layers.z_ui.table_view.models.RowHeaderModel
import com.example.budgetvalue.layers.z_ui.table_view.view_holders.CellViewHolder

class MyTableViewAdapter(val context: Context) : AbstractTableAdapter<ColumnHeaderModel, RowHeaderModel, CellModel>() {
    override fun onCreateColumnHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: ColumnHeaderModel?,
        columnPosition: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: RowHeaderModel?,
        rowPosition: Int
    ) {
        val rowHeaderViewModel = holder as RowHeaderViewModel
    }

//    fun onBindRowHeaderViewHolder(
//        holder: AbstractViewHolder,
//        p_jValue: Any,
//        p_nYPosition: Int
//    ) {
//        val rowHeaderModel = p_jValue as RowHeaderModel
//        val rowHeaderViewHolder: RowHeaderViewHolder = holder as RowHeaderViewHolder
//        rowHeaderViewHolder.row_header_textview.setText(rowHeaderModel.getData())
//    }

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        TODO("Not yet implemented")
    }

    override fun getCellItemViewType(position: Int): Int {
        TODO("Not yet implemented")
    }

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        val v = LayoutInflater.from(context).inflate(
            R.layout.tableview_cell_layout,
            parent, false
        )
        return CellViewHolder(v)
    }

    override fun onCreateCornerView(parent: ViewGroup): View {
        return View.inflate(context, R.layout.tableview_basic, parent)
    }

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: CellModel?,
        columnPosition: Int,
        rowPosition: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun getColumnHeaderItemViewType(position: Int): Int {
        TODO("Not yet implemented")
    }

    override fun getRowHeaderItemViewType(position: Int): Int {
        TODO("Not yet implemented")
    }
}