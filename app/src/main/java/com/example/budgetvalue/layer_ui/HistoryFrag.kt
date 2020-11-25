package com.example.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.budgetvalue.App
import com.example.budgetvalue.R
import com.tminus1010.tmcommonkotlin.misc.createVmFactory

class HistoryFrag: Fragment(R.layout.frag_history) {
    val app by lazy { requireActivity().application as App }
    val categoriesAppVM by lazy { app.appComponent.getCategoriesAppVM() }
    val repo by lazy { app.appComponent.getRepo() }
    val historyVM: HistoryVM by activityViewModels { createVmFactory { HistoryVM(repo, categoriesAppVM) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}