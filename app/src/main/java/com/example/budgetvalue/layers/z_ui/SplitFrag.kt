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
        view.myTableView_1.finishInit(listOf(
            TableViewColumnData(requireContext(), "Category", categoriesVM.categories.value.map {it.name}),
            TableViewColumnData(requireContext(),"Spent", generateLipsum(5)),
            TableViewColumnData(requireContext(),"Income", generateLipsum(6)),
            TableViewColumnData(requireContext(),"Budgeted", generateLipsum(7))
        ))
    }
}