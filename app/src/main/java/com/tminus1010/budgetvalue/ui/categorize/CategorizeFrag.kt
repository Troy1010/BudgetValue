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
import com.tminus1010.budgetvalue._unrestructured.transactions.view.ReceiptCategorizationHostFrag
import com.tminus1010.budgetvalue.app.TransactionsInteractor
import com.tminus1010.budgetvalue.data.service.MoshiWithCategoriesProvider
import com.tminus1010.budgetvalue.databinding.FragCategorizeBinding
import com.tminus1010.budgetvalue.framework.android.GenViewHolder2
import com.tminus1010.budgetvalue.framework.android.LifecycleRVAdapter2
import com.tminus1010.budgetvalue.framework.android.viewBinding
import com.tminus1010.budgetvalue.ui.category_settings.CategoryDetailsFrag
import com.tminus1010.budgetvalue.ui.choose_categories.ChooseCategoriesSharedVM
import com.tminus1010.budgetvalue.ui.errors.Errors
import com.tminus1010.budgetvalue.ui.futures.CreateFutureFrag
import com.tminus1010.budgetvalue.ui.futures.FutureDetailsFrag
import com.tminus1010.budgetvalue.ui.set_search_texts.SetSearchTextsSharedVM
import com.tminus1010.budgetvalue.ui.set_string.SetStringFrag
import com.tminus1010.budgetvalue.ui.set_string.SetStringSharedVM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.toPX
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class CategorizeFrag : Fragment(R.layout.frag_categorize) {
    private val vb by viewBinding(FragCategorizeBinding::bind)
    private val viewModel by activityViewModels<CategorizeVM>()

    @Inject
    lateinit var moshiWithCategoriesProvider: MoshiWithCategoriesProvider

    @Inject
    lateinit var chooseCategoriesSharedVM: ChooseCategoriesSharedVM

    @Inject
    lateinit var errors: Errors

    @Inject
    lateinit var setStringSharedVM: SetStringSharedVM

    @Inject
    lateinit var setSearchTextsSharedVM: SetSearchTextsSharedVM

    @Inject
    lateinit var transactionsInteractor: TransactionsInteractor

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Events
        errors.observe(viewLifecycleOwner) { throw it }
        viewModel.navToCreateFuture.observe(viewLifecycleOwner) { CreateFutureFrag.navTo(nav, setSearchTextsSharedVM, transactionsInteractor) }
        viewModel.navToNewCategory.observe(viewLifecycleOwner) { CategoryDetailsFrag.navTo(nav, null, true) }
        viewModel.navToCategorySettings.observe(viewLifecycleOwner) { CategoryDetailsFrag.navTo(nav, it.name, false) }
        viewModel.navToReplayOrFutureDetails.observe(viewLifecycleOwner) { FutureDetailsFrag.navTo(nav, moshiWithCategoriesProvider, it, chooseCategoriesSharedVM, setSearchTextsSharedVM) }
        viewModel.navToReceiptCategorization.observe(viewLifecycleOwner) { ReceiptCategorizationHostFrag.navTo(nav, it, moshiWithCategoriesProvider) }
        viewModel.navToEditStringForAddTransactionToFutureWithEdit.observe(viewLifecycleOwner) { SetStringFrag.navTo(nav, it, setStringSharedVM) }
        // # State
        vb.textviewDate.bind(viewModel.date) { text = it }
        vb.textviewAmount.bind(viewModel.latestUncategorizedTransactionAmount) { text = it }
        vb.textviewDescription.bind(viewModel.latestUncategorizedTransactionDescription) { text = it }
        vb.textviewAmountLeft.bind(viewModel.uncategorizedSpendsSize) { text = it }
        val spanSize = if (requireContext().resources.configuration.fontScale <= 1.0) 3 else 2
        vb.recyclerviewCategories.addItemDecoration(LayoutMarginDecoration(spanSize, 8.toPX(requireContext())))
        vb.recyclerviewCategories.layoutManager = GridLayoutManager(requireActivity(), spanSize, GridLayoutManager.VERTICAL, false)
        vb.recyclerviewCategories.bind(viewModel.items) {
            val viewItemRecipes = it.map { it.toViewItemRecipe(requireContext()) }
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
