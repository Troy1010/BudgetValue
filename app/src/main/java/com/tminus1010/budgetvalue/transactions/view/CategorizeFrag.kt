package com.tminus1010.budgetvalue.transactions.view

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import com.thekhaeng.recyclerviewmargin.LayoutMarginDecoration
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.all_features.data.MoshiWithCategoriesProvider
import com.tminus1010.budgetvalue.all_features.framework.view.GenViewHolder2
import com.tminus1010.budgetvalue.all_features.framework.view.LifecycleRVAdapter2
import com.tminus1010.budgetvalue.all_features.framework.view.viewBinding
import com.tminus1010.budgetvalue.all_features.presentation.Errors
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.categories.ui.CategorySettingsFrag
import com.tminus1010.budgetvalue.databinding.FragCategorizeBinding
import com.tminus1010.budgetvalue.replay_or_future.view.CreateFuture2Frag
import com.tminus1010.budgetvalue.replay_or_future.view.ReplayOrFutureDetailsFrag
import com.tminus1010.budgetvalue.transactions.presentation.CategorizeVM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.toPX
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@AndroidEntryPoint
class CategorizeFrag : Fragment(R.layout.frag_categorize) {
    private val vb by viewBinding(FragCategorizeBinding::bind)
    private val vm by activityViewModels<CategorizeVM>()

    @Inject
    lateinit var categoryAmountsConverter: CategoryAmountsConverter

    @Inject
    lateinit var moshiWithCategoriesProvider: MoshiWithCategoriesProvider

    @Inject
    lateinit var errors: Errors

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Events
        errors.observe(viewLifecycleOwner) { throw it }
        vm.navToCreateFuture2.observe(viewLifecycleOwner) { CreateFuture2Frag.navTo(nav) }
        vm.navToSplit.observe(viewLifecycleOwner) { SplitFrag.navTo(nav, it) }
        vm.navToNewCategory.observe(viewLifecycleOwner) { CategorySettingsFrag.navTo(nav, null, true) }
        vm.navToCategorySettings.observe(viewLifecycleOwner) { CategorySettingsFrag.navTo(nav, it.name, false) }
        vm.navToReplayOrFutureDetails.observe(viewLifecycleOwner) { ReplayOrFutureDetailsFrag.navTo(nav, moshiWithCategoriesProvider, it) }
        vm.navToSelectReplay.observe(viewLifecycleOwner) { nav.navigate(R.id.useReplayFrag) }
        vm.navToReceiptCategorization.observe(viewLifecycleOwner) { ReceiptCategorizationHostFrag.navTo(nav, it, categoryAmountsConverter) }
        // # State
        vb.textviewDate.bind(vm.date) { text = it }
        vb.textviewAmount.bind(vm.latestUncategorizedTransactionAmount) { text = it }
        vb.textviewDescription.bind(vm.latestUncategorizedTransactionDescription) { text = it }
        vb.textviewAmountLeft.bind(vm.uncategorizedSpendsSize) { text = it }
        val spanSize = if (requireContext().resources.configuration.fontScale <= 1.0) 3 else 2
        vb.recyclerviewCategories.addItemDecoration(LayoutMarginDecoration(spanSize, 8.toPX(requireContext())))
        vb.recyclerviewCategories.layoutManager = GridLayoutManager(requireActivity(), spanSize, GridLayoutManager.VERTICAL, false)
        vb.recyclerviewCategories.bind(vm.recipeGrid.map { it.map { it.toViewItemRecipe(requireContext()) } }) { viewItemRecipes ->
            adapter = object : LifecycleRVAdapter2<GenViewHolder2<ViewBinding>>() {
                override fun onCreateViewHolder(parent: ViewGroup, i: Int): GenViewHolder2<ViewBinding> =
                    GenViewHolder2(viewItemRecipes[i].createVB(parent))

                override fun getItemCount() = viewItemRecipes.size
                override fun onLifecycleAttached(holder: GenViewHolder2<ViewBinding>) {
                    viewItemRecipes[holder.adapterPosition].bind(holder.vb)
                }

                override fun getItemViewType(position: Int) = position
            }
        }
        vb.buttonsview.bind(vm.buttons) { buttons = it }
    }
}
