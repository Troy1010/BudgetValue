package com.tminus1010.buva.ui.choose_categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.android.GridMarginDecoration
import com.tminus1010.buva.all_layers.android.LifecycleRVAdapter2
import com.tminus1010.buva.all_layers.android.viewBinding
import com.tminus1010.buva.databinding.FragChooseCategoriesBinding
import com.tminus1010.buva.databinding.ItemCategoryBtnBinding
import com.tminus1010.tmcommonkotlin.androidx.GenViewHolder
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class ChooseCategoriesFrag : Fragment(R.layout.frag_choose_categories) {
    private val vb by viewBinding(FragChooseCategoriesBinding::bind)
    private val selectCategoriesVM by viewModels<ChooseCategoriesVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # State
        vb.buttonsview.bind(selectCategoriesVM.buttons) { buttons = it }
        val spanSize = if (requireContext().resources.configuration.fontScale <= 1.0) 3 else 2
        vb.recyclerviewCategories.addItemDecoration(GridMarginDecoration(spanSize, 7, false))
        vb.recyclerviewCategories.layoutManager =
            GridLayoutManager(requireActivity(), spanSize, GridLayoutManager.VERTICAL, false)
        vb.recyclerviewCategories.bind(selectCategoriesVM.categoryButtonVMItems.map { it.map { it.toViewItemRecipe(requireContext()) } }) { viewItemRecipes ->
            adapter = object : LifecycleRVAdapter2<GenViewHolder<ItemCategoryBtnBinding>>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                    GenViewHolder(ItemCategoryBtnBinding.inflate(LayoutInflater.from(requireContext()), parent, false))

                override fun getItemCount() = viewItemRecipes.size
                override fun onLifecycleAttached(holder: GenViewHolder<ItemCategoryBtnBinding>) {
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