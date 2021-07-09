package com.tminus1010.budgetvalue.transactions.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.thekhaeng.recyclerviewmargin.LayoutMarginDecoration
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.middleware.ui.ButtonRVItem
import com.tminus1010.budgetvalue._core.middleware.ui.GenViewHolder2
import com.tminus1010.budgetvalue._core.middleware.ui.LifecycleRVAdapter
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue._core.ui.data_binding.bindButtonRVItem
import com.tminus1010.budgetvalue.categories.CategoriesVM
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.CategorySettingsVM
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.categories.ui.CategorySettingsFrag
import com.tminus1010.budgetvalue.databinding.FragCategorizeBinding
import com.tminus1010.budgetvalue.databinding.ItemButtonBinding
import com.tminus1010.budgetvalue.databinding.ItemCategoryBtnBinding
import com.tminus1010.budgetvalue.transactions.CategorizeTransactionsAdvancedVM
import com.tminus1010.budgetvalue.transactions.CategorizeTransactionsVM
import com.tminus1010.budgetvalue.transactions.TransactionsVM
import com.tminus1010.budgetvalue.transactions.domain.CategorizeAdvancedDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.toPX
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class CategorizeFrag : Fragment(R.layout.frag_categorize) {
    private val vb by viewBinding(FragCategorizeBinding::bind)
    private val categorizeTransactionsVM: CategorizeTransactionsVM by activityViewModels()
    private val categoriesVM: CategoriesVM by activityViewModels()
    private val transactionsVM: TransactionsVM by activityViewModels()
    private val categorySelectionVM: CategorySelectionVM by navGraphViewModels(R.id.categorizeNestedGraph) { defaultViewModelProviderFactory }
    private val categorizeTransactionsAdvancedVM: CategorizeTransactionsAdvancedVM by activityViewModels()
    private val categorySettingsVM: CategorySettingsVM by navGraphViewModels(R.id.categorizeNestedGraph) { defaultViewModelProviderFactory }

    @Inject
    lateinit var categorizeAdvancedDomain: CategorizeAdvancedDomain
    var btns = emptyList<ButtonRVItem>()
        set(value) {
            field = value; vb.recyclerviewButtons.adapter?.notifyDataSetChanged()
        }
    var categories = emptyList<Category>()
        set(value) {
            val shouldNotifyDataSetChanged = field.size != value.size
            field = value
            if (shouldNotifyDataSetChanged) vb.recyclerviewCategories.adapter?.notifyDataSetChanged()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Mediation
        categorizeTransactionsVM.setup(categorySelectionVM)
        // # Some of SelectionMode
        categorySelectionVM.inSelectionMode.observe(viewLifecycleOwner) { inSelectionMode ->
            // ## inSelectionMode
            vb.root.children
                .filter { it != vb.recyclerviewCategories && it != vb.recyclerviewButtons }
                .forEach { it.alpha = if (inSelectionMode) 0.5F else 1F }
        }
        // # Navigation
        vb.root.bind(categorizeTransactionsVM.navToSplit) {
            categorizeTransactionsAdvancedVM.setup(it, categorySelectionVM)
            nav.navigate(R.id.action_categorizeFrag_to_splitTransactionFrag)
        }
        // # TextViews
        vb.textviewDate.bind(categorizeTransactionsVM.date) { text = it }
        vb.textviewAmount.bind(categorizeTransactionsVM.latestUncategorizedTransactionAmount) { text = it }
        vb.textviewDescription.bind(categorizeTransactionsVM.latestUncategorizedTransactionDescription) { text = it }
        vb.textviewAmountLeft.bind(transactionsVM.uncategorizedSpendsSize) { text = it }
        // # Categories RecyclerView
        categoriesVM.userCategories.observe(viewLifecycleOwner) { categories = it }
        vb.recyclerviewCategories.addItemDecoration(LayoutMarginDecoration(3, 8.toPX(requireContext())))
        vb.recyclerviewCategories.layoutManager =
            GridLayoutManager(requireActivity(), 3, GridLayoutManager.VERTICAL, false)
        vb.recyclerviewCategories.adapter = object : LifecycleRVAdapter<GenViewHolder2<ItemCategoryBtnBinding>>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                ItemCategoryBtnBinding.inflate(LayoutInflater.from(requireContext()), parent, false)
                    .let { GenViewHolder2(it) }

            override fun onBindViewHolder(holder: GenViewHolder2<ItemCategoryBtnBinding>, position: Int) {
                val selectionModeAction = {
                    if (categories[holder.adapterPosition] !in categorySelectionVM.selectedCategories.value!!)
                        categorySelectionVM.selectCategories(categories[holder.adapterPosition])
                    else
                        categorySelectionVM.unselectCategories(categories[holder.adapterPosition])
                }
                holder.vb.btnCategory.text = categories[holder.adapterPosition].name
                holder.vb.btnCategory.setOnClickListener {
                    if (categorySelectionVM.inSelectionMode.value!!)
                        selectionModeAction()
                    else if (categorizeTransactionsVM.isTransactionAvailable.value!!)
                        categorizeTransactionsVM.userSimpleCategorize(categories[holder.adapterPosition])
                }
                holder.vb.btnCategory.setOnLongClickListener { selectionModeAction(); true }
            }

            override fun getItemCount() = categories.size
            override fun onViewAttachedToWindow(holder: GenViewHolder2<ItemCategoryBtnBinding>, lifecycle: LifecycleOwner) {
                val categoryName = categories[holder.adapterPosition].name
                categorySelectionVM.selectedCategories.observe(lifecycle) { selectedCategories ->
                    holder.vb.btnCategory.alpha =
                        if (selectedCategories.isEmpty() || categoryName in selectedCategories.map { it.name }) 1F
                        else 0.5F
                }
            }
        }
        // # Button RecyclerView
        vb.recyclerviewButtons.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        vb.recyclerviewButtons.addItemDecoration(LayoutMarginDecoration(8.toPX(requireContext())))
        vb.recyclerviewButtons.adapter = object : LifecycleRVAdapter<GenViewHolder2<ItemButtonBinding>>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                ItemButtonBinding.inflate(LayoutInflater.from(requireContext()), parent, false)
                    .let { GenViewHolder2(it) }

            override fun onViewAttachedToWindow(holder: GenViewHolder2<ItemButtonBinding>, lifecycle: LifecycleOwner) {
                holder.vb.btnItem.bindButtonRVItem(lifecycle, btns[holder.adapterPosition])
            }

            override fun getItemCount() = btns.size
        }
        categorySelectionVM.inSelectionMode.observe(viewLifecycleOwner) { inSelectionMode ->
            btns = listOfNotNull(
                if (inSelectionMode)
                    ButtonRVItem(
                        title = "Split",
                        isEnabled = categorizeTransactionsVM.isTransactionAvailable,
                        onClick = {
                            categorizeTransactionsAdvancedVM.setup(
                                categoryAmounts = null,
                                categorySelectionVM = categorySelectionVM
                            )
                            nav.navigate(R.id.action_categorizeFrag_to_splitTransactionFrag)
                        }
                    )
                else null,
                if (inSelectionMode)
                    ButtonRVItem(
                        title = "Settings",
                        isEnabled = categorySelectionVM.selectedCategories.map { it.size == 1 },
                        onClick = {
                            CategorySettingsFrag.navigateTo(
                                nav = nav,
                                source = this,
                                categorySettingsVM = categorySettingsVM,
                                categoryName = categorySelectionVM.selectedCategories.value!!.first().name,
                                isForNewCategory = false
                            )
                            categorySelectionVM.clearSelection().subscribe()
                        }
                    )
                else null,
                if (!inSelectionMode)
                    ButtonRVItem(
                        title = "Redo",
                        isEnabled = categorizeTransactionsVM.isRedoAvailable,
                        onClick = { categorizeTransactionsVM.userRedo() })
                else null,
                if (!inSelectionMode)
                    ButtonRVItem(
                        title = "Undo",
                        isEnabled = categorizeTransactionsVM.isUndoAvailable,
                        onClick = { categorizeTransactionsVM.userUndo() })
                else null,
                if (!inSelectionMode)
                    ButtonRVItem(
                        title = "Replay by %",
                        isEnabled = categorizeTransactionsVM.isReplayAvailable,
                        onLongClick = { categorizeTransactionsVM.userNavToSplitWithReplayByPercentage() },
                        onClick = { categorizeTransactionsVM.userReplay() })
                else null,
                if (!inSelectionMode)
                    ButtonRVItem(
                        title = "Make New Category",
                        onClick = {
                            CategorySettingsFrag.navigateTo(
                                nav = nav,
                                source = this,
                                categorySettingsVM = categorySettingsVM,
                                categoryName = null,
                                isForNewCategory = true
                            )
                        }
                    )
                else null,
            ).reversed()
        }
    }
}
