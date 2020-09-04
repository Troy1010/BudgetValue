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
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.R
import com.example.budgetvalue.layers.z_ui.misc.*
import com.example.budgetvalue.util.*
import com.example.tmcommonkotlin.logz
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.tableview_layout.view.*
import java.lang.Exception
import java.lang.Math.max
import kotlin.math.ceil


class MyTableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    val mainView: View = View.inflate(context, R.layout.tableview_layout, this)
    var columnCount = 0
    var dataZ = listOf<String>()
    var headers = listOf<String>()
    var bInitialized = false
    var firstPass = true
    val minColWidths = BehaviorSubject.createDefault<List<Int>>(listOf<Int>())
    val intrinsicColWidths = BehaviorSubject.createDefault<List<Int>>(listOf<Int>())
    val tableViewWidth = BehaviorSubject.createDefault<Int>(0)
    val columnWidthsObservable = combineLatestAsTuple(
        minColWidths,
        intrinsicColWidths,
        tableViewWidth)
        .filter { it.first.isNotEmpty() && it.second.isNotEmpty() && (tableViewWidth.value!=0) }
        .logSubscribe2("Before map that runs generateColWidths")
        .map {
            logz("About to run generateColumnWidths from chain")
            generateColumnWidths(it.first, it.second, it.third)
        }
        .publish().refCount()
        .logSubscribe2("columnWidthsObservable")
        .toBehaviorSubjectWithDefault(listOf())

    @SuppressLint("InflateParams")
    val defaultHeaderFactory = {
        val view = LayoutInflater.from(context).inflate(R.layout.tableview_header, null, false)
//        view.layoutParams = LinearLayout.LayoutParams(
//            300,
//            100
//        )
//        val width = columnWidths.getOrNull(i) ?: 0
        view
    }
    val defaultHeaderBindAction = { view: View, data: Any ->
        view as TextView
        view.text = data as String
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
    val cellBindAction by lazy { cellViewBindAction__ }
    lateinit var headerViewFactory__: () -> View
    lateinit var headerViewBindAction__: (View, Any) -> Unit
    lateinit var cellViewFactory__: () -> View
    lateinit var cellViewBindAction__: (View, Any) -> Unit
    fun finishInit(
        mHeaders: List<String>,
        data: List<String>,
        headerViewFactory_: (() -> View) = defaultHeaderFactory,
        headerViewBindAction_: ((View, Any) -> Unit) = defaultHeaderBindAction,
        cellViewFactory_: (() -> View) = defaultCellViewFactory,
        cellViewBindAction_: ((View, Any) -> Unit) = defaultCellViewBindAction
    ) {
        logz("finishInit`Open")
        headerViewFactory__ = headerViewFactory_
        headerViewBindAction__ = headerViewBindAction_
        cellViewFactory__ = cellViewFactory_
        cellViewBindAction__ = cellViewBindAction_
        // initialize
        mainView.recyclerview_data.requestLayout()
        //  find width
        (mainView.parent as View).requestLayout()
//        (mainView.parent as View).measure(MeasureSpec.EXACTLY, MeasureSpec.EXACTLY)
//        logz("dsfgsd${(mainView.parent as View).measuredWidth}")
//        (mainView.parent as View).measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
//        logz("dsfgtrewtrtysd${(mainView.parent as View).measuredWidth}")
//        (mainView.parent as View).addOnLayoutChangeListener(object: View.OnLayoutChangeListener {
//            override fun onLayoutChange(
//                p0: View?,
//                p1: Int,
//                p2: Int,
//                p3: Int,
//                p4: Int,
//                p5: Int,
//                p6: Int,
//                p7: Int,
//                p8: Int
//            ) {
//                mainView.requestLayout()
//                logz("Layout is changing..")
//            }
//
//        })
//        mainView.parent.requestLayout()
//        logz("widthhhdhh:${(mainView.parent as View).measuredWidth}")
//        logz("widthhhhgh:${(mainView.parent as View).intrinsicWidth2}")
//        logz("widthhhfhh:${(mainView.parent as View).exactWidth}")
        //
        setColumnHeaderData(mHeaders)
        mainView.recyclerview_column_header.adapter = GenericRecyclerViewAdapter5(
            headerFactory,
            { holder, view->
                headerBindAction(view, headers[holder.adapterPosition])
                logz("columnWidths.getOrNull(holder.adapterPosition) ?: 0:${columnWidthsObservable.value.getOrNull(holder.adapterPosition) ?: 0}")
                view.layoutParams = RecyclerView.LayoutParams(
                    { columnWidthsObservable.value.getOrNull(holder.adapterPosition) ?: 0 }(),
                    200
                )
            },
            { headers.size }
        )
        //
        setTableData(data)
        mainView.recyclerview_column_header.layoutManager = MyTableViewLayoutManager2(
            this,
            HORIZONTAL
        )
//        mainView.recyclerview_column_header.layoutManager = LinearLayoutManager(
//            context,
//            HORIZONTAL,
//            false
//        )
        val dividerItemDecoration = TableViewDecoration(
            context as Activity,
            VERTICAL,
            true
        )
        mainView.recyclerview_data.addItemDecoration(dividerItemDecoration)
        mainView.recyclerview_data.adapter = MyTableViewDataRecyclerViewAdapter(
            rowFactory,
            cellBindAction,
            { dataZ.size / columnCount + if ((dataZ.size % columnCount) == 0) 0 else 1 },
            { columnCount },
            { dataZ })
        mainView.recyclerview_data.layoutManager = MyTableViewLayoutManager(this)
        //
        bInitialized = true
        //
        intrinsicColWidths.onNext(
            generateIntrinsicWidths(rowFactory, cellBindAction, data, columnCount)
        )
        minColWidths.onNext(
            generateMinWidths(headerFactory, headerBindAction, headers)
        )
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

    class InitializationNotFinished(msg: String = "${MyTableView::class.simpleName}`finishInit() must be called before layout is completed") : Exception(msg)

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        logz("``MyTableView`onMeasure`measuredWidth:${measuredWidth}")
//    }
//    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
//        logz("onLayout`measuredWidth:${measuredWidth}")
//        if (!bInitialized) throw InitializationNotFinished()
//        if (firstPass) {
//            firstPass=false
//            recyclerview_column_header.adapter?.notifyDataSetChanged()
//        } else {
//            super.onLayout(changed, left, top, right, bottom)
//        }
//    }

//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        logz("OnAttachToWindow`measuredWidth:${measuredWidth}")
//    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        logz("onSizeChanged`Open`w:${w}")
        super.onSizeChanged(w, h, oldw, oldh)
        tableViewWidth.onNext(w)
        // trigger another layout pass after tableViewWidth has been determined.
        recyclerview_column_header.adapter?.notifyDataSetChanged()
    }

    // Give a list of strings
    // Who decides which layout to use? What if there are different layouts? Who handles how the binding happens?
    // How about: List: Any. If it's a string, cool. If it's a view, also cool.
    //    Okay, but then how can you have compile-time errors?
    // How about: 2 inputs. List<String> and HashMap<Position, View>
    fun setColumnHeaderData(mHeaders: List<String>) {
        headers = mHeaders
        mainView.recyclerview_column_header.adapter?.notifyDataSetChanged()
        columnCount = mHeaders.size
    }

    fun setTableData(dataZZ: List<String>) {
        dataZ = dataZZ
        mainView.recyclerview_data.adapter?.notifyDataSetChanged()
    }

    fun generateIntrinsicWidths(
        rowFactory: () -> LinearLayout,
        cellBindAction: (View, Any) -> Unit,
        data: List<String>,
        columnCount: Int
    ): List<Int> {
        val intrinsicWidths = ArrayList<Int>()
        val view = rowFactory()
        for ((i, x) in data.withIndex()) {
            val viewChild = view[i % columnCount]
            cellBindAction(viewChild, x)
            intrinsicWidths.add(
                viewChild.intrinsicWidth2
            )
        }
        logz("intrinsicWidths:${intrinsicWidths}")
        return intrinsicWidths
    }

    fun generateMinWidths(
        cellFactory: () -> View,
        cellBindAction: (View, Any) -> Unit,
        data: List<String>
    ): List<Int> {
        val minWidths = ArrayList<Int>()
        for (s in data) {
            val view = cellFactory()
            cellBindAction(view, s)
            minWidths.add(view.intrinsicWidth2)
        }
        logz("minWidths:${minWidths}")
        return minWidths
    }

    fun generateColumnWidths(
        minWidths: List<Int>,
        intrinsicWidths: List<Int>,
        parentWidth: Int
    ): List<Int> {
        val columnCount = minWidths.size
        logz("generateColumnWidths`Open. columnCount:${columnCount} parentWidth:${parentWidth}")
        //trigger: data set changed. input: data, layout. output: views will be correct size
        // define column widths
        val columnWidths = arrayListOfZeros(columnCount)
        for ((i, intrinsicWidth) in intrinsicWidths.withIndex()) {
            columnWidths[i % columnCount] =
                columnWidths[i % columnCount].coerceAtLeast(intrinsicWidth)
        }
        while (columnWidths.sum() < parentWidth) {
            for (i in columnWidths.indices) {
                columnWidths[i] = columnWidths[i] + 1
            }
        }
        if (columnWidths.sum() > parentWidth) {
            val ratio = parentWidth.toDouble() / columnWidths.sum().toDouble()
            for (i in columnWidths.indices) {
                columnWidths[i] = max(minWidths[i], ceil(columnWidths[i] * ratio).toInt()+10)
            }
        }
        var loopCount = 0
        var i = 0
        while ((columnWidths.sum() > parentWidth) && (loopCount<10000)) {
            i = (i+1)%columnCount
            columnWidths[i] = max(minWidths[i], columnWidths[i] - 1)
            loopCount++
        }
        logz("generateColumnWidths`Close. columnWidths:${columnWidths}. sum:${columnWidths.sum()} vs ${parentWidth}. minWidths.sum():${minWidths.sum()}")
        return columnWidths
    }
}

