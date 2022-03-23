package com.tminus1010.budgetvalue.all_features.ui.create_future

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.easyText2
import com.tminus1010.budgetvalue.all_features.framework.view.viewBinding
import com.tminus1010.budgetvalue.databinding.FragCreateFuture2Binding
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine


@AndroidEntryPoint
class CreateFuture2Frag : Fragment(R.layout.frag_create_future_2) {
    private val vb by viewBinding(FragCreateFuture2Binding::bind)
    private val viewModel by viewModels<CreateFuture2VM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Events
        viewModel.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        viewModel.navToCategorySelection.observe(viewLifecycleOwner) { nav.navigate(R.id.selectCategoriesFrag) }
        viewModel.navToChooseTransaction.observe(viewLifecycleOwner) { nav.navigate(R.id.chooseTransactionFrag) }
        viewModel.navToSetSearchTexts.observe(viewLifecycleOwner) { nav.navigate(R.id.setSearchTextsFrag) }
        viewModel.saveReplayDialogBox.observe(viewLifecycleOwner) {
            val editText = EditText(requireContext())
            editText.easyText2 = it
            AlertDialog.Builder(requireContext())
                .setMessage("What would you like to name this replay?")
                .setView(editText)
                .setPositiveButton("Submit") { _, _ -> viewModel.userTrySubmitWithName(editText.easyText2!!) }
                .setNegativeButton("Cancel") { _, _ -> }
                .show()
        }
        // # State
        vb.tmTableViewOtherInput.bind(viewModel.otherInput) {
            initialize(
                recipeGrid = it.map { it.map { it.toViewItemRecipe(requireContext()) } },
                shouldFitItemWidthsInsideTable = true,
            )
        }
        vb.tmTableViewCategoryAmounts.bind(combine(viewModel.recipeGrid, viewModel.dividerMap) { a, b -> Pair(a, b) }) { (recipeGrid, dividerMap) ->
            initialize(
                recipeGrid = recipeGrid.map { it.map { it.toViewItemRecipe(requireContext()) } },
                dividerMap = dividerMap.mapValues { it.value.toViewItemRecipe(requireContext()) },
                shouldFitItemWidthsInsideTable = true,
            )
        }
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.createFuture2Frag)
        }
    }
}