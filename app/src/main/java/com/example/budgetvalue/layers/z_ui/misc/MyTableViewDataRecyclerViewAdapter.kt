package com.example.budgetvalue.layers.z_ui.misc

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.layers.z_ui.views.MyTableView
import com.example.tmcommonkotlin.logz
import io.reactivex.rxjava3.subjects.PublishSubject

class MyTableViewDataRecyclerViewAdapter(
    var context: Context,
    val tableView: MyTableView,
    val getRowCount: () -> Int,
    val getColumnCount: () -> Int,
    val getData: () -> List<String>,
    val getColumnWidths: () -> List<Int>
): RecyclerView.Adapter<MyTableViewDataRecyclerViewAdapter.ViewHolder>() {

    val streamDataChanged = PublishSubject.create<Unit>().also {
        this.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                it.onNext(Unit)
            }
        })
    }

    fun setLayoutParams(rowView:LinearLayout, columnCount: Int, columnWidths: List<Int>) {
        for (i in 0 until columnCount) {
            val width = columnWidths.getOrNull(i) ?: 0
            rowView[i].layoutParams = LinearLayout.LayoutParams(
                width,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }

    fun createRowView(): LinearLayout {
        logz("createRowView`Open. getColumnWidths():${getColumnWidths()}")
        val view = LinearLayout(context)
        for (i in 0 until getColumnCount()) {
            val textView = TextView(context)
            textView.setTextColor(Color.WHITE)

            val width = getColumnWidths().getOrNull(i) ?: 0
            textView.layoutParams = LinearLayout.LayoutParams(
                width,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            view.addView(textView)
        }
        return view
    }
    fun bindDataAction(textView: TextView, s:String) {
        textView.text = s
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(createRowView())
    }

    override fun getItemCount(): Int = getRowCount()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder.itemView
        (view as LinearLayout)
        for ((i, child) in view.children.withIndex()) {
            (child as TextView)
            try {
                bindDataAction(child, getData()[holder.adapterPosition * getColumnCount() + i])
            } catch (e: java.lang.IndexOutOfBoundsException) {
            }
        }
    }
}