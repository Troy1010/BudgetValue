package com.example.budgetvalue.layers.z_ui.TMTableView

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.example.budgetvalue.R
import com.example.budgetvalue.layers.z_ui.TMTableView.ColumnWidthCalculator.generateColumnWidths
import com.example.budgetvalue.layers.z_ui.TMTableView.ColumnWidthCalculator.generateIntrinsicWidths
import com.example.budgetvalue.layers.z_ui.TMTableView.ColumnWidthCalculator.generateMinWidths
import com.example.budgetvalue.util.*
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.tableview_layout.view.*

class TMTableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    init { View.inflate(context, R.layout.tableview_layout, this) }
    val minColWidths = BehaviorSubject.create<List<Int>>()
    val intrinsicColWidths = BehaviorSubject.create<List<List<Int>>>()
    val tableViewWidth = BehaviorSubject.create<Int>()
    val columnWidthsObservable = combineLatestAsTuple(minColWidths, intrinsicColWidths, tableViewWidth)
        .filter { it.first.isNotEmpty() && it.second.isNotEmpty() && (tableViewWidth.value!=0) }
        .map { generateColumnWidths(it.first, it.second, it.third) }
        .toBehaviorSubjectWithDefault(listOf())

    fun setRecipes(recipe2D: List<List<ICellRecipe>>) {
        recyclerview_tier1.adapter = RecyclerViewAdapter(context, { recipe2D }, columnWidthsObservable)
        recyclerview_tier1.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        recyclerview_tier1.addItemDecoration(Decoration(context, Decoration.VERTICAL, true))
        //
        minColWidths.onNext(generateMinWidths(recipe2D[0]))
        intrinsicColWidths.onNext(generateIntrinsicWidths(recipe2D))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        tableViewWidth.onNext(w)
        // recyclerview viewholders must be re-created
        recyclerview_tier1.adapter?.notifyDataSetChanged()
    }

}

