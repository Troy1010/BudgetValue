package com.example.budgetvalue.layers.z_ui.table_view.view_holders

import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.example.budgetvalue.R
import com.example.budgetvalue.layers.z_ui.table_view.models.CellModel
import kotlinx.android.synthetic.main.tableview_cell_layout.view.*

class CellViewHolder(itemView: View) : AbstractViewHolder(itemView) {
    fun setCellModel(p_jModel: CellModel, pColumnPosition: Int) {

        // Change textView align by column
//        itemView.cell_data.gravity = ColumnHeaderViewHolder.COLUMN_TEXT_ALIGNS.get(pColumnPosition) or
//                Gravity.CENTER_VERTICAL
        itemView.cell_data.gravity = Gravity.CENTER_VERTICAL

        // Set text
        itemView.cell_data.text = p_jModel.data.toString()

        // It is necessary to remeasure itself.
        itemView.cell_container.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
        itemView.cell_data.requestLayout()
    }

    override fun setSelected(p_nSelectionState: SelectionState) {
        super.setSelected(p_nSelectionState)
        if (p_nSelectionState == SelectionState.SELECTED) {
            itemView.cell_data.setTextColor(
                ContextCompat.getColor(
                    itemView.cell_data.context,
                    R.color.selected_text_color
                )
            )
        } else {
            itemView.cell_data.setTextColor(
                ContextCompat.getColor(
                    itemView.cell_data.context,
                    R.color.unselected_text_color
                )
            )
        }
    }
}