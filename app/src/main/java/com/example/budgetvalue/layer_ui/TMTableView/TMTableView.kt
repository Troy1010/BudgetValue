package com.example.budgetvalue.layer_ui.TMTableView

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.example.budgetvalue.R
import com.example.budgetvalue.combineLatestAsTuple
import com.example.budgetvalue.layer_ui.TMTableView.ColumnWidthCalculator.generateColumnWidths
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.tableview_layout.view.*

class TMTableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    var tableView: View? = null
    val tableViewWidth = BehaviorSubject.create<Int>()
    val _recipe2D = BehaviorSubject.create<List<List<IRecipe>>>()

    init {
        // # Inflate measureMe
        // measureMe needs to be inflated to trigger onSizeChanged
        View.inflate(context, R.layout.tableview_measure_me, this)
        // # Observe
        combineLatestAsTuple(_recipe2D, tableViewWidth)
            .map { (recipe2D, tableViewWidth) -> Pair(recipe2D, generateColumnWidths(recipe2D, tableViewWidth)) }
            .subscribe { (recipe2D, columnWidths) -> inflateAndBind(recipe2D, columnWidths) }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w != oldw) tableViewWidth.onNext(w)
    }

    fun setRecipes(recipe2D: List<List<IRecipe>>) {
        _recipe2D.onNext(recipe2D)
    }

    fun inflateAndBind(recipe2D: List<List<IRecipe>>, columnWidths: List<Int>) {
        // # Inflate tableView
        if (tableView == null) tableView = View.inflate(context, R.layout.tableview_layout, this)
        // # Column Headers
        frame_headers.removeAllViews()
        val row = createRow(context, recipe2D[0])
        bindRow(row, recipe2D[0], columnWidths)
        frame_headers.addView(row)
        frame_headers.setPadding(0, 0, 0, 0)
        // # Cells
        recyclerview_tier1.adapter = RecyclerViewRecipeAdapter(
            context,
            { ArrayList(recipe2D).also { it.removeAt(0) } },
            columnWidths
        )
        recyclerview_tier1.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        recyclerview_tier1.addItemDecoration(Decoration(context, Decoration.VERTICAL, true))
    }
}

