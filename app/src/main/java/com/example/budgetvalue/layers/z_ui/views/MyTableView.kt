package com.example.budgetvalue.layers.z_ui.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.example.budgetvalue.R
import com.example.budgetvalue.layers.z_ui.misc.*
import com.example.budgetvalue.util.*
import com.example.tmcommonkotlin.logz
import kotlinx.android.synthetic.main.tableview_layout.view.*
import java.lang.Exception
import kotlin.math.floor


class MyTableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    val mainView: View = View.inflate(context, R.layout.tableview_layout, this)
    var columnCount = 0
    var dataZ = listOf<String>()
    var columnWidths = ArrayList<Int>()
    var bInitialized = false

    @SuppressLint("InflateParams")
    val defaultHeaderFactory = {
        LayoutInflater.from(context).inflate(R.layout.tableview_header, null, false)
    }
    val defaultHeaderBindAction = { view: View, data: Any ->
        view as TextView
        view.text = data as String
        view.setDimToWrapContent()
    }
    val defaultCellViewFactory = {
        val textView = TextView(context)
        textView.setTextColor(Color.WHITE)
        textView
    }
    val defaultCellViewBindAction = { view: View, data: Any ->
        view as TextView
        view.text = data as String
    }
    val headerFactory by lazy { headerViewFactory__ }
    val headerBindAction by lazy { headerViewBindAction__ }
    val cellViewFactory by lazy { cellViewFactory__ }
    val cellViewBindAction by lazy { cellViewBindAction__ }
    lateinit var headerViewFactory__: () -> View
    lateinit var headerViewBindAction__: (View, Any) -> Unit
    lateinit var cellViewFactory__: () -> View
    lateinit var cellViewBindAction__: (View, Any) -> Unit
    fun finishInit(
        headers: List<String>,
        data: List<String>,
        headerViewFactory_: (() -> View) = defaultHeaderFactory,
        headerViewBindAction_: ((View, Any) -> Unit) = defaultHeaderBindAction,
        cellViewFactory_: (() -> View) = defaultCellViewFactory,
        cellViewBindAction_: ((View, Any) -> Unit) = defaultCellViewBindAction
    ) {
        headerViewFactory__ = headerViewFactory_
        headerViewBindAction__ = headerViewBindAction_
        cellViewFactory__ = cellViewFactory_
        cellViewBindAction__ = cellViewBindAction_
        // initialize
        setColumnHeaderData(headers)
        setTableData(data)
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
            rowFactory,
            cellViewBindAction,
            { dataZ.size / columnCount + if ((dataZ.size % columnCount) == 0) 0 else 1 },
            { columnCount },
            { dataZ })
        mainView.recyclerview_data.layoutManager = MyTableViewLayoutManager(
            context,
            mainView.recyclerview_data.adapter!! as MyTableViewDataRecyclerViewAdapter, this
        )
        mainView.requestLayout()
        bInitialized = true
    }

    val rowFactory = {
        val view = LinearLayout(context)
        for (i in 0 until columnCount) {
            val cellView = cellViewFactory()
            val width = columnWidths.getOrNull(i) ?: 0
            cellView.layoutParams = LinearLayout.LayoutParams(
                width,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            view.addView(cellView)
        }
        view
    }

    class InitializationNotFinished(msg: String = "${MyTableView::class.simpleName}`finishInit() must be called before layout is completed") : Exception(msg)
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (!bInitialized) throw InitializationNotFinished()
    }

    // Give a list of strings
    // Who decides which layout to use? What if there are different layouts? Who handles how the binding happens?
    // How about: List: Any. If it's a string, cool. If it's a view, also cool.
    //    Okay, but then how can you have compile-time errors?
    // How about: 2 inputs. List<String> and HashMap<Position, View>
    fun setColumnHeaderData(headers: List<String>) {
        columnCount = headers.size
        mainView.recyclerview_column_header.adapter = GenericRecyclerViewAdapter5(
            headerFactory,
            { holder, view-> headerBindAction(view, headers[holder.adapterPosition]) },
            { headers.size }
        )
    }

    fun setTableData(dataZZ: List<String>) {
        dataZ = dataZZ
        mainView.recyclerview_data.adapter?.notifyDataSetChanged()
    }

    fun generateIntrinsicWidths(
        adapter: MyTableViewDataRecyclerViewAdapter,
        data: List<String>,
        columnCount: Int
    ): List<Int> {
        val intrinsicWidths = ArrayList<Int>()
        val view = rowFactory()
        for ((i, x) in data.withIndex()) {
            val viewChild = view[i % columnCount]
            cellViewBindAction(viewChild, x)
            intrinsicWidths.add(
                viewChild.intrinsicWidth2
            )
        }
        logz("intrinsicWidths:${intrinsicWidths}")
        return intrinsicWidths
    }

    fun initColumnWidths(
        adapter: MyTableViewDataRecyclerViewAdapter,
        intrinsicWidths: List<Int>,
        columnCount: Int,
        width: Int
    ) {
        logz("generateColumnWidths`open. adapter:${adapter}, columnCount:${columnCount}, width:${width}")
        //trigger: data set changed. input: data, layout. output: views will be correct size
        // define column widths
        val columnWidths_ = arrayListOfZeros(columnCount)
        for ((i, intrinsicWidth) in intrinsicWidths.withIndex()) {
            columnWidths_[i % columnCount] =
                columnWidths_[i % columnCount].coerceAtLeast(intrinsicWidth)
        }
        if (columnWidths_.sum() > width) {
            val ratio = width.toDouble() / columnWidths_.sum().toDouble()
            for (i in columnWidths_.indices) {
                columnWidths_[i] = floor(columnWidths_[i] * ratio).toInt()
            }
        }
        logz("columnWidths:${columnWidths_}")
        columnWidths = columnWidths_
    }
}

