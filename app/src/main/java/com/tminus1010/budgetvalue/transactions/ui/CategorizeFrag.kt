package com.tminus1010.budgetvalue.transactions.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thekhaeng.recyclerviewmargin.LayoutMarginDecoration
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.toPX
import com.tminus1010.budgetvalue._core.middleware.ui.ButtonPartial
import com.tminus1010.budgetvalue._core.middleware.ui.GenViewHolder2
import com.tminus1010.budgetvalue._core.middleware.ui.bindIncoming
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue._core.middleware.unbox
import com.tminus1010.budgetvalue._core.ui.data_binding.bindButtonPartial
import com.tminus1010.budgetvalue._core.ui.data_binding.bindText
import com.tminus1010.budgetvalue.categories.CategoriesVM
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.databinding.FragCategorizeBinding
import com.tminus1010.budgetvalue.databinding.ItemButtonBinding
import com.tminus1010.budgetvalue.databinding.ItemCategoryBtnBinding
import com.tminus1010.budgetvalue.transactions.CategorizeTransactionsVM
import com.tminus1010.budgetvalue.transactions.TransactionsVM
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers


@AndroidEntryPoint
class CategorizeFrag : Fragment(R.layout.frag_categorize) {
    val categorizeTransactionsVM by activityViewModels<CategorizeTransactionsVM>()
    val categoriesVM: CategoriesVM by activityViewModels()
    val transactionsVM by activityViewModels<TransactionsVM>()
    val categorySelectionVM: CategorySelectionVM by activityViewModels()
    val vb by viewBinding(FragCategorizeBinding::bind)
    var btns = emptyList<ButtonPartial>()
        set(value) { field = value; vb.recyclerviewButtons.adapter?.notifyDataSetChanged() }
    var categories = emptyList<Category>()
        set(value) {
            val shouldNotifyDataSetChanged = field.size != value.size
            field = value
            if (shouldNotifyDataSetChanged)vb.recyclerviewCategories.adapter?.notifyDataSetChanged()
        }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Mediation
        categorySelectionVM.clearSelection() // TODO("Bind this VM to a navGraph")
        // # Observe categorySelectionVM.state
        categorySelectionVM.state.observe(viewLifecycleOwner) { state ->
            // ## inSelectionMode
            vb.root.children
                .filter { it != vb.recyclerviewCategories && it != vb.recyclerviewButtons }
                .forEach { it.alpha = if (state.inSelectionMode) 0.5F else 1F }
        }
        // # TextViews
        vb.textviewDate.bindText(categorizeTransactionsVM.date)
        vb.textviewAmount.bindIncoming(categorizeTransactionsVM.transactionBox)
        { it.unbox?.defaultAmount?.toString() ?: "" }
        vb.textviewDescription.bindIncoming(categorizeTransactionsVM.transactionBox)
        { it.unbox?.description ?: "" }
        vb.textviewAmountLeft.bindIncoming(viewLifecycleOwner, transactionsVM.uncategorizedSpendsSize)
        // # Categories RecyclerView
        categoriesVM.userCategories
            .observeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) { categories = it }
        vb.recyclerviewCategories.addItemDecoration(LayoutMarginDecoration(3, 8.toPX(requireContext())))
        vb.recyclerviewCategories.layoutManager =
            GridLayoutManager(requireActivity(), 3, GridLayoutManager.VERTICAL, false)
        vb.recyclerviewCategories.adapter = object : RecyclerView.Adapter<GenViewHolder2<ItemCategoryBtnBinding>>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                ItemCategoryBtnBinding.inflate(LayoutInflater.from(requireContext()), parent, false)
                    .let { GenViewHolder2(it) }

            override fun onBindViewHolder(holder: GenViewHolder2<ItemCategoryBtnBinding>, position: Int) {
                val category = categories[position]
                val selectionModeAction = {
                    if (category !in categorySelectionVM.selectedCategories.value!!)
                        categorySelectionVM.selectCategory(category)
                    else
                        categorySelectionVM.unselectCategory(category)
                }
                categorySelectionVM.selectedCategories.observe(viewLifecycleOwner) { selectedCategories ->
                    holder.vb.btnCategory.alpha =
                        if (selectedCategories.isEmpty() || category in selectedCategories) 1F
                        else 0.5F
                }
                holder.vb.btnCategory.apply {
                    text = category.name
                    setOnClickListener {
                        if (categorySelectionVM.inSelectionMode.value!!) selectionModeAction()
                        else categorizeTransactionsVM.finishTransactionWithCategory(category)
                    }
                    setOnLongClickListener { selectionModeAction(); true }
                }
            }

            override fun getItemCount() = categories.size
        }
        // # Button RecyclerView
        categorySelectionVM.inSelectionMode.observe(viewLifecycleOwner) { btns = if (it)
            listOf(
                ButtonPartial("Delete") {
                    AlertDialog.Builder(requireContext())
                        .setMessage(listOf(
                            "Are you sure you want to delete these categories?\n",
                            *categorySelectionVM.selectedCategories.value!!.map { "\t${it.name}" }.toTypedArray()
                        ).joinToString("\n"))
                        .setPositiveButton("Yes") { _, _ -> categorySelectionVM.deleteSelectedCategories() }
                        .setNegativeButton("No") { _, _ -> }
                        .show()
                },
                ButtonPartial("Split", categorizeTransactionsVM.isTransactionAvailable ) {
                    nav.navigate(R.id.action_categorizeFrag_to_splitTransactionFrag)
                },
                ButtonPartial("Clear selection") { categorySelectionVM.clearSelection() },
            )
            else listOf(
                ButtonPartial("Make New Category") { nav.navigate(R.id.action_categorizeFrag_to_newCategoryFrag) },
            )
        }
        vb.recyclerviewButtons.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        vb.recyclerviewButtons.addItemDecoration(LayoutMarginDecoration(8.toPX(requireContext())))
        vb.recyclerviewButtons.adapter = object : RecyclerView.Adapter<GenViewHolder2<ItemButtonBinding>>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                ItemButtonBinding.inflate(LayoutInflater.from(requireContext()), parent, false)
                    .let { GenViewHolder2(it) }

            override fun onBindViewHolder(holder: GenViewHolder2<ItemButtonBinding>, position: Int) =
                holder.vb.btnItem.bindButtonPartial(viewLifecycleOwner, btns[position]) // TODO("This is the wrong lifecycle owner")

            override fun getItemCount() = btns.size
        }
    }
}
