package com.example.budgetvalue.layers.z_ui.views

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.example.budgetvalue.R
import com.example.budgetvalue.layers.z_ui.misc.DecorationForRecyclerView
import com.example.budgetvalue.layers.z_ui.misc.GenericRecyclerViewAdapter3
import com.example.budgetvalue.util.logSubscribe2
import com.example.budgetvalue.util.measuredWidth2
import com.example.budgetvalue.util.onNext
import com.example.budgetvalue.util.setDimToWrapContent
import com.example.tmcommonkotlin.GenericRecyclerViewAdapter2
import com.example.tmcommonkotlin.logz
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.tableview_layout.view.*
import java.util.concurrent.TimeUnit
import kotlin.math.floor


class MyTableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    val mainView : View = View.inflate(context, R.layout.tableview_layout, this)
    val streamBindHappened = PublishSubject.create<Unit>()
    val streamBindsAreDone = streamBindHappened.buffer(500, TimeUnit.MILLISECONDS)
        .filter{ !it.isNullOrEmpty() }
    var columnCount = 0
    // Give a list of strings
    // Who decides which layout to use? What if there are different layouts? Who handles how the binding happens?
    // How about: List: Any. If it's a string, cool. If it's a view, also cool.
    //    Okay, but then how can you have compile-time errors?
    // How about: 2 inputs. List<String> and HashMap<Position, View>
    fun setColumnHeaderData(headers: List<String>) {
        columnCount = headers.size
        mainView.recyclerview_column_header.adapter = GenericRecyclerViewAdapter2(object :
            GenericRecyclerViewAdapter2.Callbacks {
            override fun bindRecyclerItem(
                holder: GenericRecyclerViewAdapter2.ViewHolder,
                view: View
            ) {
                view as TextView
                view.text = headers[holder.adapterPosition]
                view.setDimToWrapContent()
                streamBindHappened.onNext()
            }

            override fun getRecyclerDataSize(): Int = headers.size
        }, context, R.layout.tableview_header)
    }
    fun setTableData(dataZ: List<String>) {
        logz("dataZ.count:${dataZ.size}")
        mainView.recyclerview_data.adapter = GenericRecyclerViewAdapter3(object :
            GenericRecyclerViewAdapter3.Callbacks {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): GenericRecyclerViewAdapter3.ViewHolder {
//                val view = LayoutInflater.from(context).inflate(R, parent, false)
                val view = LinearLayout(context)
                for (i in 0 until columnCount) {
                    val textView = TextView(context)
                    textView.setTextColor(Color.WHITE)
                    view.addView(textView)
                }
                return GenericRecyclerViewAdapter3.ViewHolder(view)
            }

            override fun bindRecyclerItem(
                holder: GenericRecyclerViewAdapter3.ViewHolder,
                view: View
            ) {
                (view as LinearLayout)
                for ((i, child) in view.children.withIndex()) {
                    (child as TextView)
                    child.text = dataZ[holder.adapterPosition * columnCount + i]
//                    val width = mainView.recyclerview_column_header.layoutManager?.getChildAt(i)?.measuredWidth2 ?: 0
                    child.layoutParams = LinearLayout.LayoutParams(
                        150,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }
                streamBindHappened.onNext()
            }

            override fun getRecyclerDataSize(): Int =
                dataZ.size / columnCount + if ((dataZ.size % columnCount) == 0) 1 else 0
        })
    }
    init {
        mainView.recyclerview_column_header.layoutManager = LinearLayoutManager(
            context,
            HORIZONTAL,
            false
        )
        mainView.recyclerview_data.layoutManager = LinearLayoutManager(context)
//        val dividerItemDecoration = DecorationForRecyclerView(
//            context as Activity,
//            VERTICAL
//        )
//        mainView.recyclerview_data.addItemDecoration(dividerItemDecoration)
        //
        streamBindsAreDone.observeOn(AndroidSchedulers.mainThread()).subscribe {
            val columnWidths = ArrayList<Int>()
            for (i in 0 until columnCount) {
                columnWidths.add(0)
            }
            // define views
            val views = ArrayList<View>().also { it.addAll(mainView.recyclerview_column_header.children) }
            for (child in mainView.recyclerview_data.children) {
                (child as LinearLayout)
                logz("child.children.toList().size:${child.children.toList().size}")
                views.addAll(child.children.toList())
            }
            for (child in views) {
                logz("child.:${(child as TextView).text}")
            }
            logz("views.count:${views.count()}")
            // define columnWidths
            for ((i, child) in views.withIndex()) {
                logz("columnWidths[i % columnCount]:${columnWidths[i % columnCount]} vs ${child.measuredWidth2}")
                columnWidths[i % columnCount] =
                    columnWidths[i % columnCount].coerceAtLeast(child.measuredWidth2)
            }
            logz("columnWidths:${columnWidths}")
            if (columnWidths.sum() > this.width) {
                val ratio = this.width.toDouble()/columnWidths.sum().toDouble()
                for (i in columnWidths.indices) {
                    columnWidths[i] = floor(columnWidths[i] * ratio).toInt()
                }
            }
            // update views
            for ((i, child) in views.withIndex()) {
                child.layoutParams.width = columnWidths[i % columnCount]
                child.requestLayout()
            }
            mainView.recyclerview_data.invalidateItemDecorations()
        }
    }
}

