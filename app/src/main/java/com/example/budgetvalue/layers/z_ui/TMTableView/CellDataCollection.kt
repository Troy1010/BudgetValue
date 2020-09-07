package com.example.budgetvalue.layers.z_ui.TMTableView

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.core.view.setPadding

class CellDataCollection<V:View,D:Any>(
    val cellViewFactory: ()->V,
    val cellBindAction: (V, D) -> Unit,
    val data: List<D>
) {
    fun toCellDatas() = ArrayList<ICellData>().also { it.addAll(data.map { CellData(cellViewFactory, cellBindAction, it) }) }
    companion object {
        fun create(context: Context, data: List<String>): CellDataCollection<TextView, String> {
            return CellDataCollection(
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
    }
}