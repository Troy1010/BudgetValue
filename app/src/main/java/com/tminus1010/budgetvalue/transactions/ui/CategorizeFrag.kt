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
import com.thekhaeng.recyclerviewmargin.LayoutMarginDecoration
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.extensions.unbox
import com.tminus1010.budgetvalue._core.middleware.ui.ButtonVMItem
import com.tminus1010.budgetvalue._core.middleware.ui.GenViewHolder2
import com.tminus1010.budgetvalue._core.middleware.ui.LifecycleRVAdapter
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.categories.CategoriesVM
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.categories.ui.CategorySettingsFrag
import com.tminus1010.budgetvalue.databinding.FragCategorizeBinding
import com.tminus1010.budgetvalue.databinding.ItemCategoryBtnBinding
import com.tminus1010.budgetvalue.replay_or_future.CreateFutureFrag
import com.tminus1010.budgetvalue.replay_or_future.ReplaysFrag
import com.tminus1010.budgetvalue.transactions.CategorizeVM
import com.tminus1010.budgetvalue.transactions.TransactionsMiscVM
import com.tminus1010.budgetvalue.transactions.domain.CategorizeAdvancedDomain
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
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

    @Inject
    lateinit var transactionsDomain: TransactionsDomain

    @Inject
    lateinit var categorizeAdvancedDomain: CategorizeAdvancedDomain
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
            vb.root.children
                .filter { it != vb.recyclerviewCategories && it != vb.buttonsview }
                .forEach { it.alpha = if (inSelectionMode) 0.5F else 1F }
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
        // # Buttons
        Observables.combineLatest(
            categorySelectionVM.inSelectionMode,
            categorizeVM.matchingReplays,
        ).observe(viewLifecycleOwner) { (inSelectionMode, matchingReplays) ->
            vb.buttonsview.buttons = listOfNotNull(
                if (inSelectionMode)
                    ButtonVMItem(
                        title = "Create Future",
                        onClick = {
                            CategorizeAdvancedFrag.navTo(
                                source = this,
                                nav = nav,
                                categorySelectionVM = categorySelectionVM,
                                transaction = transactionsDomain.firstUncategorizedSpend.value?.first,
                                replayOrFuture = null,
                                categorizeAdvancedType = CategorizeAdvancedFrag.CategorizeAdvancedType.CREATE_FUTURE,
                            )
                        }
                    )
                else null,
                if (inSelectionMode)
                    ButtonVMItem(
                        title = "Split",
                        isEnabled = categorizeVM.isTransactionAvailable,
                        onClick = {
                            CategorizeAdvancedFrag.navTo(
                                source = this,
                                nav = nav,
                                categorySelectionVM = categorySelectionVM,
                                transaction = transactionsDomain.firstUncategorizedSpend.value!!.first!!,
                                replayOrFuture = null,
                                categorizeAdvancedType = CategorizeAdvancedFrag.CategorizeAdvancedType.SPLIT,
                            )
                        }
                    )
                else null,
                if (inSelectionMode)
                    ButtonVMItem(
                        title = "Category Settings",
                        isEnabled = categorySelectionVM.selectedCategories.map { it.size == 1 },
                        onClick = {
                            CategorySettingsFrag.navTo(
                                source = this,
                                nav = nav,
                                categoryName = categorySelectionVM.selectedCategories.value!!.first().name,
                                isForNewCategory = false
                            )
                            categorySelectionVM.clearSelection().subscribe()
                        }
                    )
                else null,
                *(if (inSelectionMode)
                    emptyList()
                else
                    matchingReplays
                        .map { replay ->
                            ButtonVMItem(
                                title = "Replay (${replay.name})",
                                onClick = { categorizeVM.userReplay(replay) },
                                onLongClick = {
                                    CategorizeAdvancedFrag.navTo(
                                        source = this,
                                        nav = nav,
                                        categorySelectionVM = categorySelectionVM,
                                        transaction = transactionsDomain.firstUncategorizedSpend.value!!.first!!,
                                        replayOrFuture = replay,
                                        categorizeAdvancedType = CategorizeAdvancedFrag.CategorizeAdvancedType.EDIT,
                                    )
                                })
                        })
                    .toTypedArray(),
                if (!inSelectionMode)
                    ButtonVMItem(
                        title = "Create Future",
                        onClick = {
                            CreateFutureFrag.navTo(nav)
                        })
                else null,
                if (!inSelectionMode)
                    ButtonVMItem(
                        title = "Browse Replays",
                        isEnabled = transactionsDomain.firstUncategorizedSpend.map { it.first != null },
                        onClick = {
                            ReplaysFrag.navTo(nav, transactionsDomain.firstUncategorizedSpend.unbox.description)
                        })
                else null,
                if (!inSelectionMode)
                    ButtonVMItem(
                        title = "Redo",
                        isEnabled = categorizeVM.isRedoAvailable,
                        onClick = { categorizeVM.userRedo() })
                else null,
                if (!inSelectionMode)
                    ButtonVMItem(
                        title = "Undo",
                        isEnabled = categorizeVM.isUndoAvailable,
                        onClick = { categorizeVM.userUndo() })
                else null,
                if (!inSelectionMode)
                    ButtonVMItem(
                        title = "Make New Category",
                        onClick = {
                            CategorySettingsFrag.navTo(
                                nav = nav,
                                source = this,
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
