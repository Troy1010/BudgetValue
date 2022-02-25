package com.tminus1010.budgetvalue.replay_or_future.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.framework.view.recipe_factories.*
import com.tminus1010.budgetvalue._core.framework.view.viewBinding
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.databinding.FragCreateFuture2Binding
import com.tminus1010.budgetvalue.databinding.FragCreateFutureBinding
import com.tminus1010.budgetvalue.replay_or_future.presentation.CreateFuture2VM
import com.tminus1010.budgetvalue.replay_or_future.presentation.CreateFutureVM
import com.tminus1010.budgetvalue.transactions.presentation.model.SearchType
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.remove
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateFuture2Frag : Fragment(R.layout.frag_create_future_2) {
    private val vb by viewBinding(FragCreateFuture2Binding::bind)
    private val createFuture2VM by viewModels<CreateFuture2VM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.createFuture2Frag)
        }
    }
}