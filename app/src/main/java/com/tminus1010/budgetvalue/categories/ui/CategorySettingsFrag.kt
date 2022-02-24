package com.tminus1010.budgetvalue.categories.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.InvalidCategoryNameException
import com.tminus1010.budgetvalue._core.all.extensions.easyEmit
import com.tminus1010.budgetvalue._core.framework.view.viewBinding
import com.tminus1010.budgetvalue.categories.CategorySettingsVM
import com.tminus1010.budgetvalue.databinding.FragCategorySettingsBinding
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.easyToast
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategorySettingsFrag : Fragment(R.layout.frag_category_settings) {
    private val vb by viewBinding(FragCategorySettingsBinding::bind)
    private val categorySettingsVM by viewModels<CategorySettingsVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Mediation
        categorySettingsVM.originalCategoryName.easyEmit(requireArguments().getString(KEY_CATEGORY_NAME, ""))
        categorySettingsVM.isForNewCategory.easyEmit(requireArguments().getBoolean(KEY_IS_FOR_NEW_CATEGORY))
        // # Events
        categorySettingsVM.errors.observe(viewLifecycleOwner) {
            when (it) {
                is InvalidCategoryNameException -> easyToast("Invalid name")
                else -> throw it
            }
        }
        categorySettingsVM.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        categorySettingsVM.showDeleteConfirmationPopup.observe(viewLifecycleOwner) {
            AlertDialog.Builder(requireContext())
                .setMessage("Are you sure you want to delete these categories?\n\t${it}")
                .setPositiveButton("Yes") { _, _ -> categorySettingsVM.userDeleteCategory() }
                .setNegativeButton("No") { _, _ -> }
                .show()
        }
        // # State
        vb.tvTitle.bind(categorySettingsVM.title) { text = it }
        vb.buttonsview.bind(categorySettingsVM.buttons) { buttons = it }
        vb.tmTableView.bind(categorySettingsVM.optionsRecipeGrid) {
            initialize(
                recipeGrid = it.map { it.map { it.toViewItemRecipe(requireContext()) } },
                shouldFitItemWidthsInsideTable = true,
            )
        }
    }

    companion object {
        const val KEY_IS_FOR_NEW_CATEGORY = "KEY_IS_FOR_NEW_CATEGORY"
        const val KEY_CATEGORY_NAME = "KEY_CATEGORY_NAME"
        fun navTo(nav: NavController, categoryName: String?, isForNewCategory: Boolean) {
            nav.navigate(
                R.id.categorySettingsFrag,
                Bundle().apply {
                    putBoolean(KEY_IS_FOR_NEW_CATEGORY, isForNewCategory)
                    putString(KEY_CATEGORY_NAME, categoryName)
                }
            )
        }
    }
}