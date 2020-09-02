package com.example.budgetvalue.layers.z_ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.budgetvalue.R
import com.example.budgetvalue.util.generateLipsum
import kotlinx.android.synthetic.main.frag_split.view.*

class SplitFrag : Fragment(R.layout.frag_split) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.myTableView_1.finishInit(
            listOf("Category", "Spent", "Income", "Budgeted"),
            generateLipsum(19)
        )
    }
}