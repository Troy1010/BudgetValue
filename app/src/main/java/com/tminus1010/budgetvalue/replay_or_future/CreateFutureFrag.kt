package com.tminus1010.budgetvalue.replay_or_future

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.databinding.FragCreateFutureBinding
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateFutureFrag : Fragment(R.layout.frag_create_future) {
    private val vb by viewBinding(FragCreateFutureBinding::bind)
    private val createFutureVM: CreateFutureVM by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createFutureVM.categoryAmountFormulaVMItems
        createFutureVM.fillCategory.observe(viewLifecycleOwner) {
//            vb.
        }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.createFutureFrag)
        }
    }
}