package com.example.budgetvalue.layer_ui.TMTableView2

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.example.budgetvalue.R
import com.example.budgetvalue.layer_ui.TMTableView.Decoration
import com.example.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.tableview_layout.view.*

class TMTableView2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    var tableView: View? = null
    val _recipe2D = BehaviorSubject.create<Iterable<Iterable<IViewItemRecipe>>>()

    init {
        _recipe2D
            .subscribe { inflateAndBind(it) }
    }

    fun setRecipes(viewItemRecipe2D: Iterable<Iterable<IViewItemRecipe>>) {
        _recipe2D.onNext(viewItemRecipe2D)
    }

    fun inflateAndBind(viewItemRecipe2D: Iterable<Iterable<IViewItemRecipe>>) {
        // # Inflate tableView
        if (tableView == null) tableView = View.inflate(context, R.layout.tableview_layout, this)
        // # Freeze columns/rows
        // TODO("Frozen columns/rows")
        // # Cells
        recyclerview_tier1.adapter = ViewItemRecipeRecyclerViewAdapter2(
            context,
            viewItemRecipe2D.toMutableList()
        )
        recyclerview_tier1.layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        recyclerview_tier1.addItemDecoration(Decoration(context, Decoration.HORIZONTAL))
    }
}

