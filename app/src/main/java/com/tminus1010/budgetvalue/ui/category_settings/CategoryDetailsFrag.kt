package com.tminus1010.budgetvalue.ui.category_settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.all_layers.InvalidCategoryNameException
import com.tminus1010.budgetvalue.all_layers.KEY1
import com.tminus1010.budgetvalue.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.data.service.MoshiProvider
import com.tminus1010.budgetvalue.databinding.FragCategoryDetailsBinding
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue.domain.TransactionMatcher
import com.tminus1010.budgetvalue.framework.android.viewBinding
import com.tminus1010.budgetvalue.ui.errors.Errors
import com.tminus1010.budgetvalue.ui.set_search_texts.SetSearchTextsFrag
import com.tminus1010.budgetvalue.ui.set_search_texts.SetSearchTextsSharedVM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
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

    @Inject
    lateinit var moshiProvider: MoshiProvider

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Setup
        viewModel.originalCategory.onNext(moshiProvider.moshi.fromJson(requireArguments().getString(KEY1, "").ifEmpty { null }))
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
        fun navTo(nav: NavController, moshiProvider: MoshiProvider, category: Category?, setSearchTextsSharedVM: SetSearchTextsSharedVM) {
            setSearchTextsSharedVM.searchTexts.adjustTo((category?.onImportTransactionMatcher as? TransactionMatcher.Multi)?.transactionMatchers?.filterIsInstance<TransactionMatcher.SearchText>()?.map { it.searchText } ?: listOfNotNull((category?.onImportTransactionMatcher as? TransactionMatcher.SearchText)?.searchText))
            nav.navigate(
                R.id.categoryDetailsFrag,
                Bundle().apply {
                    putString(KEY1, moshiProvider.moshi.toJson(category))
                },
            )
        }
    }
}