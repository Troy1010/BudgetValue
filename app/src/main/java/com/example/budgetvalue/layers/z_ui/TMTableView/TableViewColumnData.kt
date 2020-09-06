package com.example.budgetvalue.layers.z_ui.TMTableView

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.core.view.setPadding
import com.example.budgetvalue.R

data class TableViewColumnData<V: View, D:Any, V2, D2> (
    val header: D,
    val headerViewFactory: ()->V,
    val headerBindAction: (V, D) -> Unit,
    val data: List<D2>,
    val cellViewFactory: ()->V2,
    val cellBindAction: (V2, D2) -> Unit
) {
    companion object {
        operator fun invoke(
            context: Context,
            header: String,
            data: List<String>
        ): TableViewColumnData<TextView, String, TextView, String> {
            return TableViewColumnData(
                header,
                { View.inflate(context, R.layout.tableview_header, null) as TextView },
                { view: TextView, s: String? -> view.text = s },
                data,
                {
                    TextView(context)
                        .apply {
                            setTextColor(Color.WHITE)
                            setPadding(10)
                        }
                },
                { view: TextView, s: String? -> view.text = s }
            )
        }
    }
}