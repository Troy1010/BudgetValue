package com.tminus1010.budgetvalue.replay_or_future.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.framework.view.viewBinding
import com.tminus1010.budgetvalue.databinding.FragCreateFuture2Binding
import com.tminus1010.budgetvalue.replay_or_future.presentation.CreateFuture2VM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateFuture2Frag : Fragment(R.layout.frag_create_future_2) {
    private val vb by viewBinding(FragCreateFuture2Binding::bind)
    private val createFuture2VM by viewModels<CreateFuture2VM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Events
        createFuture2VM.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        createFuture2VM.navToCategorySelection.observe(viewLifecycleOwner) { nav.navigate(R.id.selectCategoriesFrag) }
        // # State
        vb.tmTableViewOtherInput.bind(createFuture2VM.otherInput) {
            initialize(
                recipeGrid = it.map { it.map { it.toViewItemRecipe(requireContext()) } },
                shouldFitItemWidthsInsideTable = true
            )
        }
        vb.tmTableViewCategoryAmounts.bind(createFuture2VM.recipeGrid) {
            initialize(
                recipeGrid = it.map { it.map { it.toViewItemRecipe(requireContext()) } },
                shouldFitItemWidthsInsideTable = true,
            )
        }
        vb.buttonsview.bind(createFuture2VM.buttons) { buttons = it }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.createFuture2Frag)
        }
    }
}