package com.example.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.budgetvalue.App
import com.example.budgetvalue.R
import com.tminus1010.tmcommonkotlin.misc.createVmFactory

class PlanFrag: Fragment(R.layout.frag_plan) {
    val appComponent by lazy { (requireActivity().application as App).appComponent }
    val planVM : PlanVM by viewModels { createVmFactory { PlanVM(appComponent.getRepo()) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupBinds()
        setupObservers()
    }

    private fun setupBinds() {
    }

    private fun setupViews() {
    }

    private fun setupObservers() {
    }
}