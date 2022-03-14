package com.tminus1010.budgetvalue.transactions.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.thekhaeng.recyclerviewmargin.LayoutMarginDecoration
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.framework.view.GenViewHolder2
import com.tminus1010.budgetvalue._core.framework.view.LifecycleRVAdapter2
import com.tminus1010.budgetvalue._core.framework.view.viewBinding
import com.tminus1010.budgetvalue._core.presentation.Errors
import com.tminus1010.budgetvalue.categories.CategoriesVM
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.ui.CategorySettingsFrag
import com.tminus1010.budgetvalue.databinding.FragCategorizeBinding
import com.tminus1010.budgetvalue.databinding.ItemCategoryBtnBinding
import com.tminus1010.budgetvalue.replay_or_future.view.CreateFuture2Frag
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.budgetvalue.transactions.presentation.CategorizeVM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.toPX
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class CategorizeFrag : Fragment(R.layout.frag_categorize) {
    private val vb by viewBinding(FragCategorizeBinding::bind)
    private val categorizeVM by activityViewModels<CategorizeVM>()
    private val categoriesVM by activityViewModels<CategoriesVM>()
    private val categorySelectionVM by navGraphViewModels<CategorySelectionVM>(R.id.categorizeNestedGraph) { defaultViewModelProviderFactory }

    @Inject
    lateinit var transactionsInteractor: TransactionsInteractor

    @Inject
    lateinit var categoryAmountsConverter: CategoryAmountsConverter

    @Inject
    lateinit var errors: Errors

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Mediation
        categorySelectionVM.selectedCategories.subscribe(categorizeVM.selectedCategories)
        categorizeVM.clearSelection.observe(viewLifecycleOwner) { categorySelectionVM.clearSelection().subscribe() }
        // # Events
        errors.observe(viewLifecycleOwner) { throw it }
        categorizeVM.navToCreateFuture2.observe(viewLifecycleOwner) { CreateFuture2Frag.navTo(nav) }
        categorizeVM.navToSplit.observe(viewLifecycleOwner) { SplitFrag.navTo(nav, it) }
        categorizeVM.navToNewCategory.observe(viewLifecycleOwner) { CategorySettingsFrag.navTo(nav, null, true) }
        categorizeVM.navToCategorySettings.observe(viewLifecycleOwner) { CategorySettingsFrag.navTo(nav, it.name, false) }
        categorizeVM.navToReplay.observe(viewLifecycleOwner) { TODO() }
        categorizeVM.navToSelectReplay.observe(viewLifecycleOwner) { nav.navigate(R.id.useReplayFrag) }
        categorizeVM.navToReceiptCategorization.observe(viewLifecycleOwner) { ReceiptCategorizationHostFrag.navTo(nav, it, categoryAmountsConverter) }
        // # State
        // ## Some of SelectionMode
        categorySelectionVM.selectedCategories.map { it.isNotEmpty() }.observe(viewLifecycleOwner) { inSelectionMode ->
            vb.root.children
                .filter { it != vb.recyclerviewCategories && it != vb.buttonsview }
                .forEach { it.alpha = if (inSelectionMode) 0.5F else 1F }
        }
        // ## TextViews
        vb.textviewDate.bind(categorizeVM.date) { text = it }
        vb.textviewAmount.bind(categorizeVM.latestUncategorizedTransactionAmount) { text = it }
        vb.textviewDescription.bind(categorizeVM.latestUncategorizedTransactionDescription) { text = it }
        vb.textviewAmountLeft.bind(categorizeVM.uncategorizedSpendsSize) { text = it }
        // ## Categories RecyclerView
        val spanSize = if (requireContext().resources.configuration.fontScale <= 1.0) 3 else 2
        vb.recyclerviewCategories.addItemDecoration(LayoutMarginDecoration(spanSize, 8.toPX(requireContext())))
        vb.recyclerviewCategories.layoutManager = GridLayoutManager(requireActivity(), spanSize, GridLayoutManager.VERTICAL, false)
        vb.recyclerviewCategories.bind(categoriesVM.userCategories) { categories ->
            adapter = object : LifecycleRVAdapter2<GenViewHolder2<ItemCategoryBtnBinding>>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                    GenViewHolder2(ItemCategoryBtnBinding.inflate(LayoutInflater.from(requireContext()), parent, false))

                override fun onBindViewHolder(holder: GenViewHolder2<ItemCategoryBtnBinding>, position: Int) {
                    val selectionModeAction = {
                        if (categories[holder.adapterPosition] !in categorySelectionVM.selectedCategories.value!!)
                            categorySelectionVM.selectCategories(categories[holder.adapterPosition])
                        else
                            categorySelectionVM.unselectCategories(categories[holder.adapterPosition])
                    }
                    holder.vb.btnCategory.text = categories[holder.adapterPosition].name
                    holder.vb.btnCategory.setOnClickListener {
                        if (categorySelectionVM.selectedCategories.value!!.isNotEmpty())
                            selectionModeAction()
                        else if (categorizeVM.isTransactionAvailable.value)
                            categorizeVM.userSimpleCategorize(categories[holder.adapterPosition])
                    }
                    holder.vb.btnCategory.setOnLongClickListener { selectionModeAction(); true }
                }

                override fun getItemCount() = categories.size

                override fun onLifecycleAttached(holder: GenViewHolder2<ItemCategoryBtnBinding>) {
                    val categoryName = categories[holder.adapterPosition].name
                    holder.vb.btnCategory.bind(categorySelectionVM.selectedCategories) {
                        alpha = if (it.isEmpty() || categoryName in it.map { it.name }) 1F else 0.5F
                    }
                }
            }
        }
        vb.buttonsview.bind(categorizeVM.buttons) { buttons = it }
    }
}
