package com.tminus1010.buva.ui.category_details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.android.viewBinding
import com.tminus1010.buva.databinding.FragCategoryDetailsBinding
import com.tminus1010.buva.domain.Category
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryDetailsFrag : Fragment(R.layout.frag_category_details) {
    private val vb by viewBinding(FragCategoryDetailsBinding::bind)
    private val viewModel by viewModels<CategoryDetailsVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # State
        vb.tvTitle.bind(viewModel.title) { text = it }
        vb.tmTableView.bind(viewModel.optionsTableView) { it.bind(this) }
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
    }

    companion object {
        fun navTo(nav: NavController, category: Category?) {
            nav.navigate(R.id.categoryDetailsFrag, Bundle().apply {
                putParcelable(KEY1, category ?: Category(""))
            })
        }
    }
}