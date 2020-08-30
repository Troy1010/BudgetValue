package com.example.budgetvalue.layers.z_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.budgetvalue.R
import com.example.budgetvalue.util.generateLipsum
import kotlinx.android.synthetic.main.frag_split.view.*

class SplitFrag : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_split, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.myTableView_1.setColumnHeaderData(listOf("Category", "Spent", "Income", "Budgeted"))
        view.myTableView_1.setTableData(generateLipsum(19))
    }
}