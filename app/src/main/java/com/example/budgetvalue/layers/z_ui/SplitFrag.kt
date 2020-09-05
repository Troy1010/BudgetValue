package com.example.budgetvalue.layers.z_ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.budgetvalue.R
import com.example.budgetvalue.layers.view_models.CategoriesVM
import com.example.budgetvalue.Orientation
import com.example.budgetvalue.util.generateLipsum
import com.example.budgetvalue.util.make1d
import com.example.tmcommonkotlin.vmFactoryFactory
import kotlinx.android.synthetic.main.frag_split.view.*

class SplitFrag : Fragment(R.layout.frag_split) {
    val categoriesVM: CategoriesVM by viewModels { vmFactoryFactory { CategoriesVM() } }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.myTableView_1.finishInit(
            listOf("Category", "Spent", "Income", "Budgeted"),
            make1d(
                Orientation.Horizontal, listOf(
                categoriesVM.categories.value.map { it.name },
                generateLipsum(5),
                generateLipsum(6),
                generateLipsum(7)
                ))
        )
    }
}