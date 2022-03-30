package com.tminus1010.budgetvalue.ui.category_settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.all_layers.InvalidCategoryNameException
import com.tminus1010.budgetvalue.all_layers.extensions.easyEmit
import com.tminus1010.budgetvalue.databinding.FragCategoryDetailsBinding
import com.tminus1010.budgetvalue.framework.android.viewBinding
import com.tminus1010.budgetvalue.ui.errors.Errors
import com.tminus1010.budgetvalue.ui.set_search_texts.SetSearchTextsFrag
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.easyToast
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CategoryDetailsFrag : Fragment(R.layout.frag_category_details) {
    private val vb by viewBinding(FragCategoryDetailsBinding::bind)
    private val viewModel by viewModels<CategoryDetailsVM>()

    @Inject
    lateinit var errors: Errors

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Setup
        viewModel.originalCategoryName.easyEmit(requireArguments().getString(KEY_CATEGORY_NAME, ""))
        viewModel.isForNewCategory.easyEmit(requireArguments().getBoolean(KEY_IS_FOR_NEW_CATEGORY))
        // # Events
        errors.observe(viewLifecycleOwner) {
            when (it) {
                is InvalidCategoryNameException -> easyToast("Invalid name")
                else -> throw it
            }
        }
        viewModel.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        viewModel.showDeleteConfirmationPopup.observe(viewLifecycleOwner) {
            AlertDialog.Builder(requireContext())
                .setMessage("Are you sure you want to delete these categories?\n\t${it}")
                .setPositiveButton("Yes") { _, _ -> viewModel.userDeleteCategory() }
                .setNegativeButton("No") { _, _ -> }
                .show()
        }
        viewModel.navToSetSearchTexts.observe(viewLifecycleOwner) { SetSearchTextsFrag.navTo(nav) }
        // # State
        vb.tvTitle.bind(viewModel.title) { text = it }
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
        vb.tmTableView.bind(viewModel.optionsTableView) { it.bind(this) }
    }

    companion object {
        const val KEY_IS_FOR_NEW_CATEGORY = "KEY_IS_FOR_NEW_CATEGORY"
        const val KEY_CATEGORY_NAME = "KEY_CATEGORY_NAME"
        fun navTo(nav: NavController, categoryName: String?, isForNewCategory: Boolean) {
            nav.navigate(
                R.id.categoryDetailsFrag,
                Bundle().apply {
                    putBoolean(KEY_IS_FOR_NEW_CATEGORY, isForNewCategory)
                    putString(KEY_CATEGORY_NAME, categoryName)
                },
            )
        }
    }
}