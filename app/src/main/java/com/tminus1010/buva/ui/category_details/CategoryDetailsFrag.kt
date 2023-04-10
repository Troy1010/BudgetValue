package com.tminus1010.buva.ui.category_details

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.android.viewBinding
import com.tminus1010.buva.databinding.FragCategoryDetailsBinding
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.ui.choose_transaction.ChooseTransactionFrag
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryDetailsFrag : Fragment(R.layout.frag_category_details) {
    private val vb by viewBinding(FragCategoryDetailsBinding::bind)
    private val categoryDetailsVM by viewModels<CategoryDetailsVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Events
        categoryDetailsVM.showDeleteConfirmationPopup.observe(viewLifecycleOwner) {
            AlertDialog.Builder(requireContext())
                .setMessage("Are you sure you want to delete these categories?\n\t${it}")
                .setPositiveButton("Yes") { _, _ -> categoryDetailsVM.userDeleteCategory() }
                .setNegativeButton("No") { _, _ -> }
                .show()
        }
        // # State
        vb.tvTitle.bind(categoryDetailsVM.title) { text = it }
        vb.buttonsview.bind(categoryDetailsVM.buttons) { buttons = it }
        vb.tmTableView.bind(categoryDetailsVM.optionsTableView) { it.bind(this) }
    }

    companion object {
        fun navTo(nav: NavController, category: Category?) {
            nav.navigate(R.id.categoryDetailsFrag, Bundle().apply {
                putParcelable(KEY1, category ?: Category(""))
            })
        }
    }
}