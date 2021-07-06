package com.tminus1010.budgetvalue.categories.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.thekhaeng.recyclerviewmargin.LayoutMarginDecoration
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.middleware.ui.ButtonRVItem
import com.tminus1010.budgetvalue._core.middleware.ui.GenViewHolder2
import com.tminus1010.budgetvalue._core.middleware.ui.LifecycleRVAdapter
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue._core.ui.data_binding.bindButtonRVItem
import com.tminus1010.budgetvalue.categories.CategorySettingsVM
import com.tminus1010.budgetvalue.databinding.FragCategorySettingsBinding
import com.tminus1010.budgetvalue.databinding.ItemButtonBinding
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.toPX
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategorySettingsFrag : Fragment(R.layout.frag_category_settings) {
    val vb by viewBinding(FragCategorySettingsBinding::bind)
    private val categorySettingsVM: CategorySettingsVM by navGraphViewModels(R.id.categorizeNestedGraph) { defaultViewModelProviderFactory }
    var btns = emptyList<ButtonRVItem>()
        set(value) { field = value.reversed(); vb.recyclerviewButtons.adapter?.notifyDataSetChanged() }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        btns = listOf(
            ButtonRVItem(
                title = "Delete",
                onClick = {
                    AlertDialog.Builder(requireContext())
                        .setMessage("Are you sure you want to delete these categories?\n${categorySettingsVM.currentCategory.name}")
                        .setPositiveButton("Yes") { _, _ ->
                            categorySettingsVM.deleteCurrentCategory()
                            nav.navigateUp()
                        }
                        .setNegativeButton("No") { _, _ -> }
                        .show()
                }
            ),
            ButtonRVItem(
                title = "Done",
                onClick = { nav.navigateUp() }
            ),
        )
    }
}