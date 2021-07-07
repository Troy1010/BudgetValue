package com.tminus1010.budgetvalue.categories.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.thekhaeng.recyclerviewmargin.LayoutMarginDecoration
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.extensions.easyText
import com.tminus1010.budgetvalue._core.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.extensions.unbox
import com.tminus1010.budgetvalue._core.middleware.ui.*
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipeFactory3
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.recipeFactories
import com.tminus1010.budgetvalue._core.ui.data_binding.bindButtonRVItem
import com.tminus1010.budgetvalue.categories.CategorySettingsVM
import com.tminus1010.budgetvalue.categories.models.CategoryType
import com.tminus1010.budgetvalue.databinding.FragCategorySettingsBinding
import com.tminus1010.budgetvalue.databinding.ItemButtonBinding
import com.tminus1010.budgetvalue.databinding.ItemSpinnerBinding
import com.tminus1010.budgetvalue.databinding.ItemTextEditBinding
import com.tminus1010.tmcommonkotlin.core.extensions.reflectXY
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.toPX
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class CategorySettingsFrag : Fragment(R.layout.frag_category_settings) {
    private val vb by viewBinding(FragCategorySettingsBinding::bind)
    private val categorySettingsVM: CategorySettingsVM by navGraphViewModels(R.id.categorizeNestedGraph) { defaultViewModelProviderFactory }
    private var btns = emptyList<ButtonRVItem>()
        set(value) {
            field = value; vb.recyclerviewButtons.adapter?.notifyDataSetChanged()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb.tvTitle.text = "Settings (${categorySettingsVM.categoryName.value!!})"
        // # TMTableView
        val defaultAmountRecipeFactory = ViewItemRecipeFactory3<ItemTextEditBinding, Observable<String>>(
            { ItemTextEditBinding.inflate(LayoutInflater.from(context)) },
            { d, vb, lifecycleOwner ->
                vb.editText.bind(d, lifecycleOwner) { easyText = it }
                vb.editText.onDone { categorySettingsVM.userUpdateDefaultAmount(it.toMoneyBigDecimal()) }
            }
        )
        val categoryTypeRecipeFactory = ViewItemRecipeFactory3<ItemSpinnerBinding, Unit>(
            { ItemSpinnerBinding.inflate(LayoutInflater.from(context)) },
            { _, vb, _ ->
                val adapter = ArrayAdapter(requireContext(), R.layout.item_text_view_2, CategoryType.getPickableValues())
                vb.spinner.adapter = adapter
                vb.spinner.setSelection(adapter.getPosition(categorySettingsVM.categoryBox.unbox.type))
                vb.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    var didFirstSelectionHappen = AtomicBoolean(false)
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        if (didFirstSelectionHappen.getAndSet(true))
                            (vb.spinner.selectedItem as CategoryType)
                                .also { categorySettingsVM.userUpdateType(it) }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) = Unit
                }
            }
        )
        vb.tmTableView.initialize(
            recipeGrid = listOf(
                listOf(
                    recipeFactories.textView.createOne("Default Amount"),
                    recipeFactories.textView.createOne("Type")
                ),
                listOf(
                    defaultAmountRecipeFactory.createOne(categorySettingsVM.categoryBox.map { it.first?.defaultAmount?.toString() ?: "" }),
                    categoryTypeRecipeFactory.createOne(Unit)
                ),
            ).reflectXY(),
            shouldFitItemWidthsInsideTable = true,
        )
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
                        .setMessage("Are you sure you want to delete these categories?\n\t${categorySettingsVM.categoryName.value!!}")
                        .setPositiveButton("Yes") { _, _ ->
                            categorySettingsVM.userDeleteCategory()
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
        ).reversed()
    }
}