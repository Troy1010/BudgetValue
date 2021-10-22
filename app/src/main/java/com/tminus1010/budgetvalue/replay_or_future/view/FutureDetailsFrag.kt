package com.tminus1010.budgetvalue.replay_or_future.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue._core.middleware.view.viewBinding
import com.tminus1010.budgetvalue.databinding.FragCreateFutureBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FutureDetailsFrag : Fragment(R.layout.frag_create_future) {
    val vb by viewBinding(FragCreateFutureBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb.buttonsview.buttons = listOf(
            ButtonVMItem(
                "title",
            ) {}
        )
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.futureDetailsFrag)
        }
    }
}