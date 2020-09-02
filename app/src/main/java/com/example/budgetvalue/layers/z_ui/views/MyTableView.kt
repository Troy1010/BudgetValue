package com.example.budgetvalue.layers.z_ui.views

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.example.budgetvalue.R
import com.example.budgetvalue.layers.z_ui.misc.*
import com.example.budgetvalue.util.*
import com.example.tmcommonkotlin.logz
import kotlinx.android.synthetic.main.tableview_layout.view.*
import java.lang.Math.max
import kotlin.math.floor


class MyTableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    val mainView : View = View.inflate(context, R.layout.tableview_layout, this)
    var columnCount = 0
    var dataZ = listOf<String>()
    var columnWidths = ArrayList<Int>()
    // Give a list of strings
    // Who decides which layout to use? What if there are different layouts? Who handles how the binding happens?
    // How about: List: Any. If it's a string, cool. If it's a view, also cool.
    //    Okay, but then how can you have compile-time errors?
    // How about: 2 inputs. List<String> and HashMap<Position, View>
    fun setColumnHeaderData(headers: List<String>) {
        columnCount = headers.size
        mainView.recyclerview_column_header.adapter = GenericRecyclerViewAdapter4(
            context, R.layout.tableview_header,
            { headers.size },
            { holder, view ->
                view as TextView
                view.text = headers[holder.adapterPosition]
                view.setDimToWrapContent()
            }
        )
    }
    fun setTableData(dataZZ: List<String>) {
        dataZ = dataZZ
        mainView.recyclerview_data.adapter?.notifyDataSetChanged()
    }

    fun generateIntrinsicWidths(adapter: MyTableViewDataRecyclerViewAdapter, data: List<String>, columnCount: Int): List<Int> {
        val intrinsicWidths = ArrayList<Int>()
        val view = adapter.createRowView()
        for ((i, x) in data.withIndex()) {
            val viewChild = view[i % columnCount]
            viewChild as TextView
            adapter.bindDataAction(viewChild, x)
            intrinsicWidths.add(
                viewChild.intrinsicWidth2
            )
        }
        logz("intrinsicWidths:${intrinsicWidths}")
        return intrinsicWidths
    }

    fun initColumnWidths(adapter: MyTableViewDataRecyclerViewAdapter, intrinsicWidths: List<Int>, columnCount:Int, width: Int) {
        logz("generateColumnWidths`open. adapter:${adapter}, columnCount:${columnCount}, width:${width}")
        //trigger: data set changed. input: data, layout. output: views will be correct size
        // define column widths
        val columnWidths_ = arrayListOfZeros(columnCount)
        for ((i, intrinsicWidth) in intrinsicWidths.withIndex()) {
            columnWidths_[i % columnCount] = columnWidths_[i % columnCount].coerceAtLeast(intrinsicWidth)
        }
        if (columnWidths_.sum() > width) {
            val ratio = width.toDouble()/columnWidths_.sum().toDouble()
            for (i in columnWidths_.indices) {
                columnWidths_[i] = floor(columnWidths_[i] * ratio).toInt()
            }
        }
        logz("columnWidths:${columnWidths_}")
        columnWidths = columnWidths_
    }


    init {
        mainView.recyclerview_column_header.layoutManager = LinearLayoutManager(
            context,
            HORIZONTAL,
            false
        )
        val dividerItemDecoration = TableViewDecoration(
            context as Activity,
            VERTICAL,
            true
        )
        mainView.recyclerview_data.addItemDecoration(dividerItemDecoration)
        mainView.recyclerview_data.adapter = MyTableViewDataRecyclerViewAdapter(
            context,
            this,
            { dataZ.size / columnCount + if ((dataZ.size % columnCount) == 0) 0 else 1 },
            { columnCount },
            { dataZ }
        ) { columnWidths }
        val qwer =mainView.recyclerview_data.adapter!!
        mainView.recyclerview_data.layoutManager = MyTableViewLayoutManager(context,
            qwer as MyTableViewDataRecyclerViewAdapter, this
        )
    }
}

