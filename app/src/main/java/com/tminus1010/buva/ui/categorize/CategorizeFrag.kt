package com.tminus1010.buva.ui.categorize

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import com.tminus1010.buva.R
import com.tminus1010.buva.app.TransactionsInteractor
import com.tminus1010.buva.data.service.MoshiProvider
import com.tminus1010.buva.data.service.MoshiWithCategoriesProvider
import com.tminus1010.buva.databinding.FragCategorizeBinding
import com.tminus1010.buva.framework.android.*
import com.tminus1010.buva.ui.category_details.CategoryDetailsFrag
import com.tminus1010.buva.ui.choose_categories.ChooseCategoriesSharedVM
import com.tminus1010.buva.ui.errors.Errors
import com.tminus1010.buva.ui.futures.CreateFutureFrag
import com.tminus1010.buva.ui.futures.FutureDetailsFrag
import com.tminus1010.buva.ui.receipt_categorization.ReceiptCategorizationHostFrag
import com.tminus1010.buva.ui.receipt_categorization_imagetotext.ReceiptCategorizationImageToTextFrag
import com.tminus1010.buva.ui.review.NoMostRecentSpendException
import com.tminus1010.buva.ui.set_search_texts.SetSearchTextsSharedVM
import com.tminus1010.buva.ui.set_string.SetStringFrag
import com.tminus1010.buva.ui.set_string.SetStringSharedVM
import com.tminus1010.tmcommonkotlin.androidx.GenViewHolder
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.easyToast
import com.tminus1010.tmcommonkotlin.view.extensions.nav
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

    @Inject
    lateinit var moshiProvider: MoshiProvider

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Events
        errors.observe(viewLifecycleOwner) {
            when(it) {
                is NoMostRecentSpendException -> easyToast("No transaction available")
                else -> throw it
            }
        }
        viewModel.navToCreateFuture.observe(viewLifecycleOwner) { CreateFutureFrag.navTo(nav, setSearchTextsSharedVM, transactionsInteractor) }
        viewModel.navToNewCategory.observe(viewLifecycleOwner) { CategoryDetailsFrag.navTo(nav, null) }
        viewModel.navToCategoryDetails.observe(viewLifecycleOwner) { CategoryDetailsFrag.navTo(nav, it) }
        viewModel.navToReplayOrFutureDetails.observe(viewLifecycleOwner) { FutureDetailsFrag.navTo(nav, moshiWithCategoriesProvider, it, chooseCategoriesSharedVM, setSearchTextsSharedVM) }
        viewModel.navToReceiptCategorization.observe(viewLifecycleOwner) { ReceiptCategorizationHostFrag.navTo(nav, it) }
        viewModel.navToReceiptCategorizationImageToText.observe(viewLifecycleOwner) { ReceiptCategorizationImageToTextFrag.navTo(nav, it, moshiWithCategoriesProvider) }
        viewModel.navToSetString.observe(viewLifecycleOwner) { SetStringFrag.navTo(nav, it, setStringSharedVM) }
        // # State
        vb.textviewDate.bind(viewModel.date) { text = it }
        vb.textviewAmount.bind(viewModel.latestUncategorizedTransactionAmount) { text = it }
        vb.textviewDescription.bind(viewModel.latestUncategorizedTransactionDescription) { text = it }
        vb.textviewAmountLeft.bind(viewModel.uncategorizedSpendsSize) { text = it }
        val spanSize = if (requireContext().resources.configuration.fontScale <= 1.0) 3 else 2
        vb.recyclerviewCategories.addItemDecoration(GridMarginDecoration(spanSize, 7, false))
        vb.recyclerviewCategories.layoutManager = GridLayoutManager(requireActivity(), spanSize, GridLayoutManager.VERTICAL, false)
        vb.recyclerviewCategories.bind(viewModel.items) {
            val viewItemRecipes = it.map { it.toViewItemRecipe(requireContext()) }
            adapter = object : LifecycleRVAdapter2<GenViewHolder<ViewBinding>>() {
                override fun onCreateViewHolder(parent: ViewGroup, i: Int): GenViewHolder<ViewBinding> =
                    GenViewHolder(viewItemRecipes[i].createVB(parent))

                override fun getItemCount() = viewItemRecipes.size
                override fun onLifecycleAttached(holder: GenViewHolder<ViewBinding>) {
                    viewItemRecipes[holder.adapterPosition].bind(holder.vb)
                }

                override fun getItemViewType(position: Int) = position
            }
        }
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
    }
}
