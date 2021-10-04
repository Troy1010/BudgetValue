package com.tminus1010.budgetvalue.categories.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.InvalidCategoryNameException
import com.tminus1010.budgetvalue._core.all.extensions.*
import com.tminus1010.budgetvalue._core.middleware.view.onDone
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue._core.presentation.model.MenuVMItem
import com.tminus1010.budgetvalue._core.middleware.view.recipe_factories.itemTextViewRB
import com.tminus1010.budgetvalue._core.middleware.view.tmTableView3.ViewItemRecipe3
import com.tminus1010.budgetvalue._core.middleware.view.viewBinding
import com.tminus1010.budgetvalue.categories.CategorySettingsVM
import com.tminus1010.budgetvalue.categories.models.CategoryType
import com.tminus1010.budgetvalue.databinding.FragCategorySettingsBinding
import com.tminus1010.budgetvalue.databinding.ItemAmountFormulaBinding
import com.tminus1010.budgetvalue.databinding.ItemEditTextBinding
import com.tminus1010.budgetvalue.databinding.ItemSpinnerBinding
import com.tminus1010.budgetvalue.transactions.app.AmountFormula
import com.tminus1010.budgetvalue.transactions.view.CategorizeFrag
import com.tminus1010.tmcommonkotlin.core.extensions.reflectXY
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.tuple.Box
import com.tminus1010.tmcommonkotlin.view.extensions.easyToast
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.Subject
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
class CategorySettingsFrag : Fragment(R.layout.frag_category_settings) {
    private val vb by viewBinding(FragCategorySettingsBinding::bind)
    private val categorySettingsVM: CategorySettingsVM by viewModels()

    @Inject
    lateinit var errorSubject: Subject<Throwable>
    private val isForNewCategory by lazy { arguments?.getBoolean(Key.IsForNewCategory.name) ?: false }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Mediation
        _setupArgs?.also { _setupArgs = null; categorySettingsVM.setup(it.first) }
        //
        errorSubject.observe(viewLifecycleOwner) {
            when (it) {
                is InvalidCategoryNameException -> easyToast("Invalid name")
                else -> throw it
            }
        }
        categorySettingsVM.navigateUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        vb.tvTitle.bind(if (isForNewCategory) Observable.just("Create a new Category") else categorySettingsVM.categoryToPush.map { "Settings (${it.name})" }) {
            text = it
        }
        vb.buttonsview.buttons = listOfNotNull(
            if (isForNewCategory) null
            else ButtonVMItem(
                title = "Delete",
                userClick = {
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
            ButtonVMItem(
                title = "Done",
                userClick = { categorySettingsVM.userSaveCategory() }
            ),
        ).reversed()
        // # TMTableView
        val defaultAmountFormulaValueRecipe = ViewItemRecipe3<ItemAmountFormulaBinding, Unit?>(
            { ItemAmountFormulaBinding.inflate(LayoutInflater.from(context)) },
            { _, vb, lifecycleOwner ->
                vb.moneyEditText.bind(categorySettingsVM.categoryToPush, lifecycleOwner) { easyText = it.defaultAmountFormula.toDisplayStr() }
                vb.moneyEditText.onDone { categorySettingsVM.userSetDefaultAmountFormulaValue(it.toMoneyBigDecimal()) }
                vb.tvPercentage.bind(categorySettingsVM.categoryToPush, lifecycleOwner) { easyVisibility = it.defaultAmountFormula is AmountFormula.Percentage }
                vb.moneyEditText.setOnCreateContextMenuListener { menu, _, _ ->
                    menu.add(
                        *listOfNotNull(
                            if (categorySettingsVM.categoryToPush.value!!.defaultAmountFormula !is AmountFormula.Percentage)
                                MenuVMItem(
                                    title = "Percentage",
                                    onClick = {
                                        categorySettingsVM.userSetDefaultAmountFormulaIsPercentage(true)
                                    })
                            else null,
                            if (categorySettingsVM.categoryToPush.value!!.defaultAmountFormula !is AmountFormula.Value)
                                MenuVMItem(
                                    title = "No Percentage",
                                    onClick = {
                                        categorySettingsVM.userSetDefaultAmountFormulaIsPercentage(false)
                                    })
                            else null,
                        ).toTypedArray()
                    )
                }
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
                val adapter = ArrayAdapter(requireContext(), R.layout.item_text_view_without_highlight, CategoryType.getPickableValues())
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
                ).map(itemTextViewRB()::create),
                listOfNotNull(
                    if (isForNewCategory) categoryNameRecipe else null,
                    defaultAmountFormulaValueRecipe,
                    categoryTypeRecipe
                ),
            ).reflectXY(),
            shouldFitItemWidthsInsideTable = true,
        )
    }

    enum class Key { IsForNewCategory }

    companion object {
        private var _setupArgs: Box<String?>? = null
        fun navTo(source: Any, nav: NavController, categoryName: String?, isForNewCategory: Boolean) {
            _setupArgs = Box(
                categoryName
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