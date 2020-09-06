package com.example.budgetvalue.layers.z_ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.budgetvalue.R
import com.example.budgetvalue.layers.view_models.CategoriesVM
import com.example.budgetvalue.Orientation
import com.example.budgetvalue.layers.z_ui.TMTableView.TableViewColumnData
import com.example.budgetvalue.util.generateLipsum
import com.example.budgetvalue.util.make1d
import com.example.tmcommonkotlin.vmFactoryFactory
import kotlinx.android.synthetic.main.frag_split.view.*

class SplitFrag : Fragment(R.layout.frag_split) {
    val categoriesVM: CategoriesVM by viewModels { vmFactoryFactory { CategoriesVM() } }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val headerFactory = { View.inflate(context, R.layout.tableview_header, null) as TextView }
        val headerBindAction = { view: TextView, s: String? ->
            view.text = s
        }
        val cellFactory = {
            TextView(context)
                .apply {
                    setTextColor(Color.WHITE)
                    setPadding(10)
                }
        }
        val cellBindAction ={ view: TextView, s: String? ->
            view.text = s
        }
        view.myTableView_1.finishInit(listOf(
            TableViewColumnData("Category",headerFactory,headerBindAction, categoriesVM.categories.value.map {it.name},cellFactory, cellBindAction),
            TableViewColumnData("Spent",headerFactory,headerBindAction, generateLipsum(5),cellFactory, cellBindAction),
            TableViewColumnData("Income",headerFactory,headerBindAction, generateLipsum(6),cellFactory, cellBindAction),
            TableViewColumnData("Budgeted",headerFactory,headerBindAction, generateLipsum(7),cellFactory, cellBindAction)
        ))
    }
}