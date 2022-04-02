package com.tminus1010.budgetvalue.ui.choose_categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.databinding.FragChooseCategoriesBinding
import com.tminus1010.budgetvalue.databinding.ItemCategoryBtnBinding
import com.tminus1010.budgetvalue.framework.android.GenViewHolder2
import com.tminus1010.budgetvalue.framework.android.LifecycleRVAdapter2
import com.tminus1010.budgetvalue.framework.android.viewBinding
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class ChooseCategoriesFrag : Fragment(R.layout.frag_choose_categories) {
    private val vb by viewBinding(FragChooseCategoriesBinding::bind)
    private val selectCategoriesVM by viewModels<ChooseCategoriesVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Events
        selectCategoriesVM.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        // # State
        vb.buttonsview.bind(selectCategoriesVM.buttons) { buttons = it }
        val spanSize = if (requireContext().resources.configuration.fontScale <= 1.0) 3 else 2
//        vb.recyclerviewCategories.addItemDecoration(LayoutMarginDecoration(spanSize, 8.toPX(requireContext())))
        vb.recyclerviewCategories.layoutManager =
            GridLayoutManager(requireActivity(), spanSize, GridLayoutManager.VERTICAL, false)
        vb.recyclerviewCategories.bind(selectCategoriesVM.categoryButtonVMItems.map { it.map { it.toViewItemRecipe(requireContext()) } }) { viewItemRecipes ->
            adapter = object : LifecycleRVAdapter2<GenViewHolder2<ItemCategoryBtnBinding>>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                    GenViewHolder2(ItemCategoryBtnBinding.inflate(LayoutInflater.from(requireContext()), parent, false))

                override fun getItemCount() = viewItemRecipes.size
                override fun onLifecycleAttached(holder: GenViewHolder2<ItemCategoryBtnBinding>) {
                    viewItemRecipes[holder.adapterPosition].bind(holder.vb)
                }
            }
        }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.chooseCategoriesFrag)
        }
    }
}