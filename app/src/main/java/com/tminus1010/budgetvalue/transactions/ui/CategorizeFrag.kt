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
import com.tminus1010.budgetvalue.transactions.CategorizeAdvancedVM
import com.tminus1010.budgetvalue.transactions.CategorizeVM
import com.tminus1010.budgetvalue.transactions.TransactionsMiscVM
import com.tminus1010.budgetvalue.transactions.domain.CategorizeAdvancedDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.toPX
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.kotlin.Observables
import javax.inject.Inject


@AndroidEntryPoint
class CategorizeFrag : Fragment(R.layout.frag_categorize) {
    private val vb by viewBinding(FragCategorizeBinding::bind)
    private val categorizeVM: CategorizeVM by activityViewModels()
    private val categoriesVM: CategoriesVM by activityViewModels()
    private val transactionsMiscVM: TransactionsMiscVM by activityViewModels()
    private val categorySelectionVM: CategorySelectionVM by navGraphViewModels(R.id.categorizeNestedGraph) { defaultViewModelProviderFactory }
    private val categorizeAdvancedVM: CategorizeAdvancedVM by activityViewModels()
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
        categorizeVM.setup(categorySelectionVM)
        // # Some of SelectionMode
        categorySelectionVM.inSelectionMode.observe(viewLifecycleOwner) { inSelectionMode ->
            // ## inSelectionMode
            vb.root.children
                .filter { it != vb.recyclerviewCategories && it != vb.recyclerviewButtons }
                .forEach { it.alpha = if (inSelectionMode) 0.5F else 1F }
        }
        // # Navigation
        vb.root.bind(categorizeVM.navToSplit) {
            categorizeAdvancedVM.setup(it, categorySelectionVM)
            nav.navigate(R.id.action_categorizeFrag_to_categorizeAdvancedFrag)
        }
        // # TextViews
        vb.textviewDate.bind(categorizeVM.date) { text = it }
        vb.textviewAmount.bind(categorizeVM.latestUncategorizedTransactionAmount) { text = it }
        vb.textviewDescription.bind(categorizeVM.latestUncategorizedTransactionDescription) { text = it }
        vb.textviewAmountLeft.bind(transactionsMiscVM.uncategorizedSpendsSize) { text = it }
        // # Categories RecyclerView
        categoriesVM.userCategories.observe(viewLifecycleOwner) { categories = it }
        vb.recyclerviewCategories.addItemDecoration(LayoutMarginDecoration(3, 8.toPX(requireContext())))
        vb.recyclerviewCategories.layoutManager =
            GridLayoutManager(requireActivity(), 3, GridLayoutManager.VERTICAL, false)
        vb.recyclerviewCategories.adapter = object : LifecycleRVAdapter<GenViewHolder2<ItemCategoryBtnBinding>>() {
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
                    if (categorySelectionVM.inSelectionMode.value!!)
                        selectionModeAction()
                    else if (categorizeVM.isTransactionAvailable.value!!)
                        categorizeVM.userSimpleCategorize(categories[holder.adapterPosition])
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
                GenViewHolder2(ItemButtonBinding.inflate(LayoutInflater.from(requireContext()), parent, false))

            override fun onViewAttachedToWindow(holder: GenViewHolder2<ItemButtonBinding>, lifecycle: LifecycleOwner) {
                holder.vb.btnItem.bindButtonRVItem(lifecycle, btns[holder.adapterPosition])
            }

            override fun getItemCount() = btns.size
        }
        Observables.combineLatest(
            categorySelectionVM.inSelectionMode,
            categorizeAdvancedVM.replays,
            categorizeAdvancedVM.transactionToPush,
        ).observe(viewLifecycleOwner) { (inSelectionMode, replays, transactionToPush) ->
            btns = listOfNotNull(
                if (inSelectionMode)
                    ButtonRVItem(
                        title = "Advanced",
                        isEnabled = categorizeVM.isTransactionAvailable,
                        onClick = {
                            CategorizeAdvancedFrag.navTo(
                                source = this,
                                nav = nav,
                                categorizeAdvancedVM = categorizeAdvancedVM,
                                categorySelectionVM = categorySelectionVM,
                                categoryAmounts = null,
                                replay = null,
                            )
                        }
                    )
                else null,
                if (inSelectionMode)
                    ButtonRVItem(
                        title = "Category Settings",
                        isEnabled = categorySelectionVM.selectedCategories.map { it.size == 1 },
                        onClick = {
                            CategorySettingsFrag.navTo(
                                source = this,
                                nav = nav,
                                categorySettingsVM = categorySettingsVM,
                                categoryName = categorySelectionVM.selectedCategories.value!!.first().name,
                                isForNewCategory = false
                            )
                            categorySelectionVM.clearSelection().subscribe()
                        }
                    )
                else null,
                *replays
                    .filter { !inSelectionMode && it.predicate(transactionToPush) }
                    .map { replay ->
                        ButtonRVItem(
                            title = "Replay (${replay.name})",
                            onClick = { categorizeVM.userReplay(replay) },
                            onLongClick = {
                                CategorizeAdvancedFrag.navTo(
                                    source = this,
                                    nav = nav,
                                    categorizeAdvancedVM = categorizeAdvancedVM,
                                    categorySelectionVM = categorySelectionVM,
                                    categoryAmounts = replay.categorize(transactionToPush).categoryAmounts,
                                    replay = replay,
                                )
                            })
                    }
                    .toTypedArray(),
                if (!inSelectionMode)
                    ButtonRVItem(
                        title = "Redo",
                        isEnabled = categorizeVM.isRedoAvailable,
                        onClick = { categorizeVM.userRedo() })
                else null,
                if (!inSelectionMode)
                    ButtonRVItem(
                        title = "Undo",
                        isEnabled = categorizeVM.isUndoAvailable,
                        onClick = { categorizeVM.userUndo() })
                else null,
                if (!inSelectionMode)
                    ButtonRVItem(
                        title = "Make New Category",
                        onClick = {
                            CategorySettingsFrag.navTo(
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
