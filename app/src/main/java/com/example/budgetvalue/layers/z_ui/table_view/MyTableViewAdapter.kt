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
import com.example.budgetvalue.layers.z_ui.table_view.view_holders.ColumnHeaderViewHolder
import com.example.budgetvalue.layers.z_ui.table_view.view_holders.RowHeaderViewHolder

class MyTableViewAdapter(val context: Context) : AbstractTableAdapter<ColumnHeaderModel, RowHeaderModel, CellModel>() {

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: ColumnHeaderModel?,
        columnPosition: Int
    ) {
        val columnHeaderViewHolder = holder as ColumnHeaderViewHolder
        columnHeaderViewHolder.setColumnHeaderModel(columnHeaderItemModel!!, columnPosition)
    }

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: CellModel?,
        columnPosition: Int,
        rowPosition: Int
    ) {
        if (holder is CellViewHolder) {
            holder.setCellModel(cellItemModel!!, columnPosition)
        }
    }

    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: RowHeaderModel?,
        rowPosition: Int
    ) {
        val rowHeaderViewHolder = holder as RowHeaderViewHolder
        rowHeaderViewHolder.row_header_textview.text = rowHeaderItemModel?.data
    }

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        val v = LayoutInflater.from(context).inflate(
            R.layout.tableview_row_header_layout,
            parent, false
        )
        return RowHeaderViewHolder(v)
    }

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        val v = LayoutInflater.from(context).inflate(
            R.layout.tableview_cell_layout,
            parent, false
        )
        return CellViewHolder(v)
    }

    override fun onCreateColumnHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder {
        val v = LayoutInflater.from(context)
            .inflate(R.layout.tableview_column_header_layout, parent, false)

        return ColumnHeaderViewHolder(v, tableView)
    }

    override fun onCreateCornerView(parent: ViewGroup): View {
        return View.inflate(context, R.layout.tableview_basic, null)
    }

    override fun getCellItemViewType(position: Int): Int {
        return 0
    }

    override fun getColumnHeaderItemViewType(position: Int): Int {
        return 0
    }

    override fun getRowHeaderItemViewType(position: Int): Int {
        return 0
    }
}