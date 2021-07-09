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
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.thekhaeng.recyclerviewmargin.LayoutMarginDecoration
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.InvalidCategoryNameException
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.extensions.easyText
import com.tminus1010.budgetvalue._core.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.extensions.unbox
import com.tminus1010.budgetvalue._core.middleware.ui.*
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipe3
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipeFactory3
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.recipeFactories
import com.tminus1010.budgetvalue._core.ui.data_binding.bindButtonRVItem
import com.tminus1010.budgetvalue.categories.CategorySettingsVM
import com.tminus1010.budgetvalue.categories.models.CategoryType
import com.tminus1010.budgetvalue.databinding.*
import com.tminus1010.budgetvalue.transactions.ui.CategorizeFrag
import com.tminus1010.tmcommonkotlin.core.extensions.reflectXY
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.toPX
import com.tminus1010.tmcommonkotlin.view.extensions.toast
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.Subject
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
class CategorySettingsFrag : Fragment(R.layout.frag_category_settings) {
    private val vb by viewBinding(FragCategorySettingsBinding::bind)
    private val categorySettingsVM: CategorySettingsVM by navGraphViewModels(R.id.categorizeNestedGraph) { defaultViewModelProviderFactory }
    private var btns = emptyList<ButtonRVItem>()
        set(value) {
            field = value; vb.recyclerviewButtons.adapter?.notifyDataSetChanged()
        }

    @Inject
    lateinit var errorSubject: Subject<Throwable>
    private val isForNewCategory by lazy { arguments?.getBoolean(Key.IsForNewCategory.name) ?: false }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        errorSubject.observe(viewLifecycleOwner) {
            when (it) {
                is InvalidCategoryNameException -> toast("Invalid name")
                else -> throw it
            }
        }
        categorySettingsVM.navigateUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        if (isForNewCategory)
            vb.tvTitle.text = "Create a new Category"
        else
            categorySettingsVM.categoryToPush.observe(viewLifecycleOwner) {
                vb.tvTitle.text = "Settings (${it.name})"
            }
        // # TMTableView
        val defaultAmountRecipe = ViewItemRecipe3<ItemMoneyEditTextBinding, Unit?>(
            { ItemMoneyEditTextBinding.inflate(LayoutInflater.from(context)) },
            { _, vb, lifecycleOwner ->
                vb.editText.bind(categorySettingsVM.categoryToPush.map { it.defaultAmount.toString() }, lifecycleOwner) { easyText = it }
                vb.editText.onDone { categorySettingsVM.userSetDefaultAmount(it.toMoneyBigDecimal()) }
            }
        )
        val categoryNameRecipe = ViewItemRecipe3<ItemEditTextBinding, Unit?>(
            { ItemEditTextBinding.inflate(LayoutInflater.from(context)) },
            { _, vb, lifecycleOwner ->
                vb.edittext.hint = "Name"
                vb.edittext.bind(categorySettingsVM.categoryToPush.map { it.name }, lifecycleOwner) { easyText = it }
                vb.edittext.onDone { categorySettingsVM.userSetName(it) }
            }
        )
        val categoryTypeRecipe = ViewItemRecipe3<ItemSpinnerBinding, Unit?>(
            { ItemSpinnerBinding.inflate(LayoutInflater.from(context)) },
            { _, vb, _ ->
                val adapter = ArrayAdapter(requireContext(), R.layout.item_text_view_2, CategoryType.getPickableValues())
                vb.spinner.adapter = adapter
                vb.spinner.setSelection(adapter.getPosition(categorySettingsVM.categoryToPush.value!!.type))
                vb.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    var didFirstSelectionHappen = AtomicBoolean(false)
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        if (didFirstSelectionHappen.getAndSet(true))
                            categorySettingsVM.userSetType(
                                type = (vb.spinner.selectedItem as CategoryType)
                            )
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) = Unit
                }
            }
        )
        vb.tmTableView.initialize(
            recipeGrid = listOf(
                listOfNotNull(
                    if (isForNewCategory) "Name" else null,
                    "Default Amount",
                    "Type"
                ).map { recipeFactories.textView.createOne(it) },
                listOfNotNull(
                    if (isForNewCategory) categoryNameRecipe else null,
                    defaultAmountRecipe,
                    categoryTypeRecipe
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
        btns = listOfNotNull(
            if (isForNewCategory) null
            else ButtonRVItem(
                title = "Delete",
                onClick = {
                    AlertDialog.Builder(requireContext())
                        .setMessage("Are you sure you want to delete these categories?\n\t${categorySettingsVM.categoryToPush.value!!.name}")
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
                onClick = { categorySettingsVM.userSaveCategory() }
            ),
        ).reversed()
    }

    enum class Key { IsForNewCategory }

    companion object {
        fun navTo(source: Any, nav: NavController, categorySettingsVM: CategorySettingsVM, categoryName: String?, isForNewCategory: Boolean) {
            categorySettingsVM.setup(
                categoryName = categoryName
            )
            nav.navigate(
                when (source) {
                    is CategorizeFrag -> R.id.action_categorizeFrag_to_categorySettingsFrag
                    else -> R.id.categorySettingsFrag
                },
                Bundle().apply { putBoolean(Key.IsForNewCategory.name, isForNewCategory) }
            )
        }
    }
}