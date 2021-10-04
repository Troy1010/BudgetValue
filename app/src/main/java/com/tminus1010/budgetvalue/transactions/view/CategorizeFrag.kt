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
import com.tminus1010.budgetvalue._core.all.extensions.bind
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue._core.middleware.view.GenViewHolder2
import com.tminus1010.budgetvalue._core.middleware.view.LifecycleRVAdapter2
import com.tminus1010.budgetvalue._core.middleware.view.viewBinding
import com.tminus1010.budgetvalue.categories.CategoriesVM
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.categories.ui.CategorySettingsFrag
import com.tminus1010.budgetvalue.databinding.FragCategorizeBinding
import com.tminus1010.budgetvalue.databinding.ItemCategoryBtnBinding
import com.tminus1010.budgetvalue.replay_or_future.CreateFutureFrag
import com.tminus1010.budgetvalue.replay_or_future.UseReplayFrag
import com.tminus1010.budgetvalue.replay_or_future.models.BasicReplay
import com.tminus1010.budgetvalue.transactions.presentation.CategorizeVM
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
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
    private val categorizeVM by activityViewModels<CategorizeVM>()
    private val categoriesVM by activityViewModels<CategoriesVM>()
    private val categorySelectionVM by navGraphViewModels<CategorySelectionVM>(R.id.categorizeNestedGraph) { defaultViewModelProviderFactory }

    @Inject
    lateinit var transactionsInteractor: TransactionsInteractor
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
        vb.textviewAmountLeft.bind(categorizeVM.uncategorizedSpendsSize) { text = it }
        // # Categories RecyclerView
        categoriesVM.userCategories.observe(viewLifecycleOwner) { categories = it }
        val spanSize = if (requireContext().resources.configuration.fontScale <= 1.0) 3 else 2
        vb.recyclerviewCategories.addItemDecoration(LayoutMarginDecoration(spanSize, 8.toPX(requireContext())))
        vb.recyclerviewCategories.layoutManager =
            GridLayoutManager(requireActivity(), spanSize, GridLayoutManager.VERTICAL, false)
        vb.recyclerviewCategories.adapter = object : LifecycleRVAdapter2<GenViewHolder2<ItemCategoryBtnBinding>>() {
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

            override fun onLifecycleAttached(holder: GenViewHolder2<ItemCategoryBtnBinding>) {
                val categoryName = categories[holder.adapterPosition].name
                holder.vb.btnCategory.bind(categorySelectionVM.selectedCategories) {
                    alpha = if (it.isEmpty() || categoryName in it.map { it.name }) 1F else 0.5F
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
                        userClick = { CreateFutureFrag.navTo(nav) }
                    )
                else null,
                if (inSelectionMode)
                    ButtonVMItem(
                        title = "Split",
                        isEnabled = categorizeVM.isTransactionAvailable,
                        userClick = { SplitFrag.navTo(nav, transactionsInteractor.mostRecentUncategorizedSpend.value!!.first!!) }
                    )
                else null,
                if (inSelectionMode)
                    ButtonVMItem(
                        title = "Category Settings",
                        isEnabled = categorySelectionVM.selectedCategories.map { it.size == 1 },
                        userClick = {
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
                                userClick = { categorizeVM.userReplay(replay) },
                                userLongClick = {
                                    ReplayFrag.navTo(
                                        nav = nav,
                                        replay = replay as BasicReplay,
                                    )
                                })
                        })
                    .toTypedArray(),
                if (!inSelectionMode)
                    ButtonVMItem(
                        title = "Use Replay",
                        userClick = { nav.navigate(R.id.useReplayFrag) })
                else null,
                if (!inSelectionMode)
                    ButtonVMItem(
                        title = "Redo",
                        isEnabled = categorizeVM.isRedoAvailable,
                        userClick = { categorizeVM.userRedo() })
                else null,
                if (!inSelectionMode)
                    ButtonVMItem(
                        title = "Undo",
                        isEnabled = categorizeVM.isUndoAvailable,
                        userClick = { categorizeVM.userUndo() })
                else null,
                if (!inSelectionMode)
                    ButtonVMItem(
                        title = "Make New Category",
                        userClick = {
                            CategorySettingsFrag.navTo(
                                nav = nav,
                                source = this,
                                categoryName = null,
                                isForNewCategory = true
                            )
                        }
                    )
                else null,
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (UseReplayFrag.chosenReplay != null)
            categorizeVM.userReplay(UseReplayFrag.chosenReplay!!)
                .also { UseReplayFrag.chosenReplay = null }
    }
}
