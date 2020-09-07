package com.example.budgetvalue.layers.z_ui.TMTableView

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.core.view.setPadding
import com.example.budgetvalue.R

data class CellData <V:View, D:Any>(
    override val viewFactory: () -> V,
    val bindAction_: (V, D) -> Unit,
    override val data: D
) : ICellData {
    override val bindAction = bindAction_ as (View, Any) -> Unit
    override val intrinsicWidth : Int
        get() {
            val view = viewFactory()
            bindAction(view, data)
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            return view.measuredWidth
        }
    companion object {
        fun create(context: Context, data: String): CellData<TextView, String> {
            return CellData(
                {
                    TextView(context)
                        .apply {
                            setTextColor(Color.WHITE)
                            setPadding(10)
                        }
                },
                { view: TextView, s: String? -> view.text = s },
                data
            )
        }
        fun create2(context: Context, data: String): CellData<TextView, String> {
            return CellData(
                { View.inflate(context, R.layout.tableview_header, null) as TextView },
                { view: TextView, s: String? -> view.text = s },
                data
            )
        }
    }
}