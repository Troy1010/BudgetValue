package com.example.budgetvalue.layers.z_ui.TMTableView

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
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.R
import com.example.budgetvalue.layers.z_ui.misc.*
import com.example.budgetvalue.layers.z_ui.TMTableView.ColumnWidthCalculator.generateColumnWidths
import com.example.budgetvalue.layers.z_ui.TMTableView.ColumnWidthCalculator.generateIntrinsicWidths
import com.example.budgetvalue.layers.z_ui.TMTableView.ColumnWidthCalculator.generateMinWidths
import com.example.budgetvalue.util.*
import com.example.tmcommonkotlin.logz
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.tableview_layout.view.*
import java.lang.Exception


// Notes
//  Give a list of strings
//  Who decides which layout to use? What if there are different layouts? Who handles how the binding happens?
//  How about: List: Any. If it's a string, cool. If it's a view, also cool.
//     Okay, but then how can you have compile-time errors?
//  How about: 2 inputs. List<String> and HashMap<Position, View>

class TMTableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    val mainView: View = View.inflate(context, R.layout.tableview_layout, this)
    var columnCount = 0
    var bInitialized = false
    val minColWidths = BehaviorSubject.create<List<Int>>()
    val intrinsicColWidths = BehaviorSubject.create<List<Int>>()
    val tableViewWidth = BehaviorSubject.create<Int>()
    val columnWidthsObservable = combineLatestAsTuple(minColWidths, intrinsicColWidths, tableViewWidth)
        .filter { it.first.isNotEmpty() && it.second.isNotEmpty() && (tableViewWidth.value!=0) }
        .map { generateColumnWidths(it.first, it.second, it.third) }
        .toBehaviorSubjectWithDefault(listOf())
    @SuppressLint("InflateParams")
    fun finishInit(headers: List<String>, data: List<String>) {
        finishInit(headers, data,
            {
                LayoutInflater.from(context).inflate(R.layout.tableview_header, null, false) as TextView
            },
            { view: TextView, s: String ->
                view.text = s
            },
            {
                TextView(context)
                    .apply {
                        setTextColor(Color.WHITE)
                        setPadding(10)
                    }
            },
            { view: TextView, s: String ->
                view.text = s
            }
        )
    }
    val headerFactory by lazy { headerViewFactory__ }
    val headerBindAction by lazy { headerViewBindAction__ }
    val cellViewFactory by lazy { cellViewFactory__ }
    val cellBindAction by lazy { cellViewBindAction__ }
    lateinit var headerViewFactory__: () -> View
    lateinit var headerViewBindAction__: (View, Any) -> Unit
    lateinit var cellViewFactory__: () -> View
    lateinit var cellViewBindAction__: (View, Any) -> Unit
    fun <V:View, D:Any> finishInit(
        headers: List<String>,
        data: List<String>,
        headerViewFactory_: (() -> V),
        headerViewBindAction_: ((V, D) -> Unit),
        cellViewFactory_: (() -> V),
        cellViewBindAction_: ((V, D) -> Unit)
    ) {
        logz("finishInit`Open")
        headerViewFactory__ = headerViewFactory_
        headerViewBindAction__ = headerViewBindAction_ as (View, Any) -> Unit
        cellViewFactory__ = cellViewFactory_
        cellViewBindAction__ = cellViewBindAction_ as (View, Any) -> Unit
        //
        columnCount = headers.size
        mainView.recyclerview_column_header.adapter = GenericRecyclerViewAdapter5(
            headerFactory,
            { holder, view->
                headerBindAction(view, headers[holder.adapterPosition])
                view.layoutParams = RecyclerView.LayoutParams(
                    columnWidthsObservable.value.getOrNull(holder.adapterPosition) ?: 0,
                    view.intrinsicHeight2
                )
            },
            { headers.size }
        )
        mainView.recyclerview_column_header.layoutManager = LinearLayoutManager(
            context,
            HORIZONTAL,
            false
        )
        //
        val dividerItemDecoration = TableViewDecoration(
            context,
            VERTICAL,
            true
        )
        mainView.recyclerview_data.addItemDecoration(dividerItemDecoration)
        mainView.recyclerview_data.adapter = MyTableViewDataRecyclerViewAdapter(
            rowFactory,
            cellBindAction,
            { data.size / columnCount + if ((data.size % columnCount) == 0) 0 else 1 },
            { columnCount },
            { data })
        mainView.recyclerview_data.layoutManager = MyTableViewLayoutManager(context)
        //
        intrinsicColWidths.onNext(
            generateIntrinsicWidths(rowFactory, cellBindAction, data, columnCount)
        )
        minColWidths.onNext(
            generateMinWidths(headerFactory, headerBindAction, headers)
        )
        //
        bInitialized = true
        //
        logz("finishInit`Close")
    }

    val rowFactory = {
        val view = LinearLayout(context)
        for (i in 0 until columnCount) {
            val cellView = cellViewFactory()
            val width = columnWidthsObservable.value.getOrNull(i) ?: 0
            cellView.layoutParams = LinearLayout.LayoutParams(
                width,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            view.addView(cellView)
        }
        view.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        view
    }

    class InitializationNotFinished(msg: String = "${TMTableView::class.simpleName}`finishInit() must be called before layout is completed") : Exception(msg)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (!bInitialized) throw InitializationNotFinished()
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        tableViewWidth.onNext(w)
        // header viewholders must be re-created when tableViewWidth changes
        recyclerview_column_header.adapter?.notifyDataSetChanged()
    }

}

