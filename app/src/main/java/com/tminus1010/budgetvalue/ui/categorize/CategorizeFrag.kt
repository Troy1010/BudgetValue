package com.tminus1010.budgetvalue.ui.categorize

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import com.thekhaeng.recyclerviewmargin.LayoutMarginDecoration
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._unrestructured.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue._unrestructured.transactions.view.ReceiptCategorizationHostFrag
import com.tminus1010.budgetvalue.app.TransactionsInteractor
import com.tminus1010.budgetvalue.data.service.MoshiWithCategoriesProvider
import com.tminus1010.budgetvalue.databinding.FragCategorizeBinding
import com.tminus1010.budgetvalue.framework.view.GenViewHolder2
import com.tminus1010.budgetvalue.framework.view.LifecycleRVAdapter2
import com.tminus1010.budgetvalue.framework.view.viewBinding
import com.tminus1010.budgetvalue.ui.category_settings.CategorySettingsFrag
import com.tminus1010.budgetvalue.ui.create_future.CreateFutureFrag
import com.tminus1010.budgetvalue.ui.create_future.ReplayOrFutureDetailsFrag
import com.tminus1010.budgetvalue.ui.edit_string.EditStringFrag
import com.tminus1010.budgetvalue.ui.edit_string.EditStringSharedVM
import com.tminus1010.budgetvalue.ui.errors.Errors
import com.tminus1010.budgetvalue.ui.select_categories.SelectCategoriesModel
import com.tminus1010.budgetvalue.ui.set_search_texts.SetSearchTextsSharedVM
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
    private val viewModel by activityViewModels<CategorizeVM>()

    @Inject
    lateinit var categoryAmountsConverter: CategoryAmountsConverter

    @Inject
    lateinit var moshiWithCategoriesProvider: MoshiWithCategoriesProvider

    @Inject
    lateinit var selectCategoriesModel: SelectCategoriesModel

    @Inject
    lateinit var errors: Errors

    @Inject
    lateinit var editStringSharedVM: EditStringSharedVM

    @Inject
    lateinit var setSearchTextsSharedVM: SetSearchTextsSharedVM

    @Inject
    lateinit var transactionsInteractor: TransactionsInteractor

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Events
        errors.observe(viewLifecycleOwner) { throw it }
        viewModel.navToCreateFuture.observe(viewLifecycleOwner) { CreateFutureFrag.navTo(nav, setSearchTextsSharedVM, transactionsInteractor) }
        viewModel.navToNewCategory.observe(viewLifecycleOwner) { CategorySettingsFrag.navTo(nav, null, true) }
        viewModel.navToCategorySettings.observe(viewLifecycleOwner) { CategorySettingsFrag.navTo(nav, it.name, false) }
        viewModel.navToReplayOrFutureDetails.observe(viewLifecycleOwner) { ReplayOrFutureDetailsFrag.navTo(nav, moshiWithCategoriesProvider, it, selectCategoriesModel, setSearchTextsSharedVM) }
        viewModel.navToReceiptCategorization.observe(viewLifecycleOwner) { ReceiptCategorizationHostFrag.navTo(nav, it, categoryAmountsConverter) }
        viewModel.navToEditStringForAddTransactionToFutureWithEdit.observe(viewLifecycleOwner) { EditStringFrag.navTo(nav, it, editStringSharedVM) }
        // # State
        vb.textviewDate.bind(viewModel.date) { text = it }
        vb.textviewAmount.bind(viewModel.latestUncategorizedTransactionAmount) { text = it }
        vb.textviewDescription.bind(viewModel.latestUncategorizedTransactionDescription) { text = it }
        vb.textviewAmountLeft.bind(viewModel.uncategorizedSpendsSize) { text = it }
        val spanSize = if (requireContext().resources.configuration.fontScale <= 1.0) 3 else 2
        vb.recyclerviewCategories.addItemDecoration(LayoutMarginDecoration(spanSize, 8.toPX(requireContext())))
        vb.recyclerviewCategories.layoutManager = GridLayoutManager(requireActivity(), spanSize, GridLayoutManager.VERTICAL, false)
        vb.recyclerviewCategories.bind(viewModel.recipeGrid.map { it.map { it.toViewItemRecipe(requireContext()) } }) { viewItemRecipes ->
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
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
    }
}
