package com.tminus1010.budgetvalue.transactions.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.all.extensions.bind
import com.tminus1010.budgetvalue._core.all.extensions.easyEmit
import com.tminus1010.budgetvalue._core.all.extensions.onClick
import com.tminus1010.budgetvalue._core.framework.view.onDone
import com.tminus1010.budgetvalue.databinding.FragChooseAmountBinding
import com.tminus1010.budgetvalue.databinding.SubfragChooseCategoryBinding
import com.tminus1010.budgetvalue.transactions.presentation.ChooseAmountVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseCategorySubFrag : Fragment(R.layout.subfrag_choose_category) {
    lateinit var vb: SubfragChooseCategoryBinding
    val chooseAmountVM by viewModels<ChooseAmountVM>() // Make a ChooseCategorySubFrag
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = SubfragChooseCategoryBinding.bind(view)
        // # Setup View
        vb.recyclerviewCategories
        // TODO: copy binding from CategorizeFrag
    }
}