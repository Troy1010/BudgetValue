package com.example.budgetvalue.layers.z_ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetvalue.R
import com.example.budgetvalue.util.setDimToWrapContent
import com.example.tmcommonkotlin.GenericRecyclerViewAdapter2
import kotlinx.android.synthetic.main.tableview_layout.view.*


class MyTableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    val mainView : View = View.inflate(context, R.layout.tableview_layout, this)
    // Give a list of strings
    // Who decides which layout to use? What if there are different layouts? Who handles how the binding happens?
    // How about: List: Any. If it's a string, cool. If it's a view, also cool.
    //    Okay, but then how can you have compile-time errors?
    // How about: 2 inputs. List<String> and HashMap<Position, View>
    fun setColumnHeaderData(headers: List<String>) {
        mainView.recyclerview_column_header.adapter = GenericRecyclerViewAdapter2(object :
            GenericRecyclerViewAdapter2.Callbacks {
            override fun bindRecyclerItem(
                holder: GenericRecyclerViewAdapter2.ViewHolder,
                view: View
            ) {
                view as TextView
                view.text = headers[holder.adapterPosition]
                view.setDimToWrapContent()
            }

            override fun getRecyclerDataSize(): Int {
                return headers.size
            }
        }, context, R.layout.tableview_basic_cell)
        setColumnSize(headers.size)
    }
    fun setColumnSize(size: Int) {
        mainView.recyclerview_data.layoutManager = GridLayoutManager(
            context,
            size,
            GridLayoutManager.VERTICAL, false
        )
    }
    fun setTableData(dataZ: List<String>) {
        mainView.recyclerview_data.adapter = GenericRecyclerViewAdapter2(object : GenericRecyclerViewAdapter2.Callbacks {
            override fun bindRecyclerItem(holder: GenericRecyclerViewAdapter2.ViewHolder, view: View) {
                view as TextView
                view.text = dataZ[holder.adapterPosition]
                view.setDimToWrapContent()
            }

            override fun getRecyclerDataSize(): Int {
                return dataZ.size
            }
        }, context, R.layout.tableview_basic_cell)
    }
    init {
        mainView.recyclerview_column_header.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        // default columnSize
        setColumnSize(4)
    }
}