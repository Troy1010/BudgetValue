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
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.example.budgetvalue.R
import com.example.budgetvalue.layers.z_ui.misc.GenericRecyclerViewAdapter3
import com.example.budgetvalue.layers.z_ui.misc.TableViewDecoration
import com.example.budgetvalue.util.*
import com.example.tmcommonkotlin.GenericRecyclerViewAdapter2
import com.example.tmcommonkotlin.logz
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.tableview_layout.view.*
import java.lang.Math.max
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
        .filter{ !it.isNullOrEmpty() }.logSubscribe2("streamBindsAreDone")
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
        val createViewAction = {
            val view = LinearLayout(context)
            for (i in 0 until columnCount) {
                val textView = TextView(context)
                textView.setTextColor(Color.WHITE)
                view.addView(textView)
            }
            view
        }
        val bindDataAction = { tv:TextView, s:String ->
            tv.text = s
            tv.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        mainView.recyclerview_data.adapter = GenericRecyclerViewAdapter3(object :
            GenericRecyclerViewAdapter3.Callbacks {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): GenericRecyclerViewAdapter3.ViewHolder {
                return GenericRecyclerViewAdapter3.ViewHolder(createViewAction())
            }

            override fun bindRecyclerItem(
                holder: GenericRecyclerViewAdapter3.ViewHolder,
                view: View
            ) {
                (view as LinearLayout)
                for ((i, child) in view.children.withIndex()) {
                    (child as TextView)
                    try {
                        bindDataAction(child, dataZ[holder.adapterPosition * columnCount + i])
                    } catch (e: java.lang.IndexOutOfBoundsException) {
                    }
                }
                streamBindHappened.onNext()
            }

            override fun getRecyclerDataSize(): Int =
                dataZ.size / columnCount + if ((dataZ.size % columnCount) == 0) 0 else 1
        }).also { adapter ->
            adapter.streamDataChanged.observeOn(AndroidSchedulers.mainThread()).startWith(
                Observable.just(
                    Unit
                )
            ).subscribe({
                //trigger: data set changed. input: data, layout. output: views will be correct size
                // define intrinsic widths
                val intrinsicWidths = ArrayList<Int>()
                val view = createViewAction()
                // inflate view
                adapter.binder.bindRecyclerItem(
                    GenericRecyclerViewAdapter3.ViewHolder(view),
                    view
                )
                for ((i,x) in dataZ.withIndex()) {
                    val viewChild = view[i%columnCount]
                    viewChild as TextView
                    bindDataAction(viewChild, x)
                    intrinsicWidths.add(
                        viewChild.intrinsicWidth2
                    )
                }
                logz("intrinsicWidths:${intrinsicWidths}")


                val views =
                    ArrayList<View>().also { it.addAll(mainView.recyclerview_column_header.children) }
                for (child in mainView.recyclerview_data.children) {
                    (child as LinearLayout)
                    views.addAll(child.children.toList())
                }
                logz("views:${views}")
                // define columnWidths
                // update current and future views
            }, {
                logz(it.narrate())
            })
        }
    }
    init {
        mainView.recyclerview_column_header.layoutManager = LinearLayoutManager(
            context,
            HORIZONTAL,
            false
        )
        mainView.recyclerview_data.layoutManager = LinearLayoutManager(context)
        val dividerItemDecoration = TableViewDecoration(
            context as Activity,
            VERTICAL,
            true
        )
        mainView.recyclerview_data.addItemDecoration(dividerItemDecoration)
        //
        streamBindsAreDone.observeOn(AndroidSchedulers.mainThread()).subscribe {
            // define views
            val views = ArrayList<View>().also { it.addAll(mainView.recyclerview_column_header.children) }
            for (child in mainView.recyclerview_data.children) {
                (child as LinearLayout)
                views.addAll(child.children.toList())
            }
            // define columnWidths
            val columnWidths = ArrayList<Int>()
            for (i in 0 until columnCount) {
                columnWidths.add(0)
            }
            val columnWidthsMins = ArrayList<Int>()
            for (i in 0 until columnCount) {
                columnWidthsMins.add(views[i].intrinsicWidth2)
            }
            for ((i, child) in views.withIndex()) {
                columnWidths[i % columnCount] =
                    columnWidths[i % columnCount].coerceAtLeast(child.intrinsicWidth2)
            }
            if (columnWidths.sum() > this.width) {
                val ratio = this.width.toDouble()/columnWidths.sum().toDouble()
                for (i in columnWidths.indices) {
                    columnWidths[i] = max(
                        columnWidthsMins[i],
                        floor(columnWidths[i] * ratio).toInt()
                    )
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

