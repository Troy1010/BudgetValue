package com.tminus1010.budgetvalue.transactions.ui

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.InvalidCategoryAmounts
import com.tminus1010.budgetvalue._core.InvalidSearchText
import com.tminus1010.budgetvalue._core.extensions.*
import com.tminus1010.budgetvalue._core.middleware.ui.ButtonVMItem
import com.tminus1010.budgetvalue._core.middleware.ui.MenuItem
import com.tminus1010.budgetvalue._core.middleware.ui.onDone
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipe3
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipeFactory3
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.recipeFactories
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.databinding.*
import com.tminus1010.budgetvalue.replay_or_future.models.IReplay
import com.tminus1010.budgetvalue.replay_or_future.models.IReplayOrFuture
import com.tminus1010.budgetvalue.transactions.CategorizeAdvancedVM
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.budgetvalue.transactions.models.SearchType
import com.tminus1010.budgetvalue.transactions.models.Transaction
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.toast
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.Subject
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
class CategorizeAdvancedFrag : Fragment(R.layout.frag_categorize_advanced) {
    private val vb by viewBinding(FragCategorizeAdvancedBinding::bind)
    private val categorizeAdvancedVM: CategorizeAdvancedVM by viewModels()
    private val categorizeAdvancedType by lazy { CategorizeAdvancedType.values()[arguments?.getInt(Key.CategorizeAdvancedType.name)!!] }

    @Inject
    lateinit var errorSubject: Subject<Throwable>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Mediation
        _setupArgs?.also { _setupArgs = null; categorizeAdvancedVM.setup(it.first, it.second, it.third) }
        //
        vb.tvTitle.bind(categorizeAdvancedVM.replayOrFuture) { (replayOrFuture) ->
            easyVisibility = replayOrFuture != null
            text = replayOrFuture?.name ?: ""
        }
        vb.tvAmountToSplit.bind(categorizeAdvancedVM.amountToCategorizeMsg) { (amountToCategorizeMsg) ->
            easyVisibility = amountToCategorizeMsg != null
            text = amountToCategorizeMsg ?: ""
        }
        categorizeAdvancedVM.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        errorSubject.observe(viewLifecycleOwner) {
            when (it) {
                is InvalidCategoryAmounts -> toast("Invalid category amounts")
                is InvalidSearchText -> toast("Invalid search text")
                is SQLiteConstraintException -> toast("Invalid duplicate name")
                else -> throw it
            }
        }
        // # TMTableView OtherInput
        vb.tmTableViewOtherInput.easyVisibility = categorizeAdvancedType == CategorizeAdvancedType.CREATE_FUTURE
        val searchTextRecipe = ViewItemRecipe3<ItemEditTextBinding, Unit?>(
            { ItemEditTextBinding.inflate(LayoutInflater.from(requireContext())) },
            { _, vb, lifecycle ->
                vb.edittext.bind(categorizeAdvancedVM.searchText, lifecycle) { if (text.toString() != it) setText(it) }
                vb.edittext.onDone { categorizeAdvancedVM.userSetSearchText(it) }
            }
        )
        val totalGuessRecipe = ViewItemRecipe3<ItemMoneyEditTextBinding, Unit?>(
            { ItemMoneyEditTextBinding.inflate(LayoutInflater.from(requireContext())) },
            { _, vb, lifecycle ->
                vb.edittext.bind(categorizeAdvancedVM.total, lifecycle) { if (text.toString() != it.toString()) setText(it.toString()) }
                vb.edittext.onDone { categorizeAdvancedVM.userSetTotalGuess(it.toMoneyBigDecimal()) }
            }
        )
        val isPermanentRecipe = ViewItemRecipe3<ItemCheckboxBinding, Unit?>(
            { ItemCheckboxBinding.inflate(LayoutInflater.from(requireContext())) },
            { _, vb, lifecycle ->
                vb.checkbox.bind(categorizeAdvancedVM.isPermanent.take(1), lifecycle) { isChecked = it }
                vb.checkbox.setOnCheckedChangeListener { _, isChecked -> categorizeAdvancedVM.userSetIsPermanent(isChecked) }
            }
        )
        val searchTypeRecipe = ViewItemRecipe3<ItemSpinnerBinding, Unit?>(
            { ItemSpinnerBinding.inflate(LayoutInflater.from(requireContext())) },
            { _, vb, _ ->
                val adapter = ArrayAdapter(requireContext(), R.layout.item_text_view_without_highlight, SearchType.values())
                vb.spinner.adapter = adapter
                vb.spinner.setSelection(adapter.getPosition(categorizeAdvancedVM.searchType.value!!))
                vb.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    var didFirstSelectionHappen = AtomicBoolean(false)
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        if (didFirstSelectionHappen.getAndSet(true))
                            categorizeAdvancedVM.userSetSearchType((vb.spinner.selectedItem as SearchType))
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) = Unit
                }
            }
        )
        if (categorizeAdvancedType == CategorizeAdvancedType.CREATE_FUTURE)
            categorizeAdvancedVM.searchType
                .map { searchType ->
                    listOfNotNull(
                        listOf(
                            recipeFactories.textView.createOne("Search Type"),
                            searchTypeRecipe,
                        ),
                        if (searchType == SearchType.DESCRIPTION) listOf(
                            recipeFactories.textView.createOne("Search Text"),
                            searchTextRecipe,
                        ) else null,
                        listOf(
                            ViewItemRecipe3(
                                { ItemTextViewBinding.inflate(LayoutInflater.from(requireContext())) },
                                { _: Any?, vb, lifecycle ->
                                    vb.textview.bind(categorizeAdvancedVM.searchType, lifecycle) { easyText = if (it == SearchType.TOTAL) "Search Total" else "Total Guess" }
                                }
                            ),
                            totalGuessRecipe,
                        ),
                        listOf(
                            recipeFactories.textView.createOne("Is Permanent"),
                            isPermanentRecipe,
                        ),
                    )
                }
                .observe(viewLifecycleOwner) { recipeGrid ->
                    vb.tmTableViewOtherInput.initialize(
                        recipeGrid = recipeGrid,
                        shouldFitItemWidthsInsideTable = true,
                        rowFreezeCount = 1,
                    )
                }

        // # TMTableView CategoryAmounts
        val categoryAmountRecipeFactory = ViewItemRecipeFactory3<ItemAmountFormulaBinding, Map.Entry<Category, Observable<AmountFormula>>>(
            { ItemAmountFormulaBinding.inflate(LayoutInflater.from(context)) },
            { (category, amountFormula), vb, lifecycle ->
                vb.moneyEditText.bind(categorizeAdvancedVM.autoFillCategory, lifecycle) {
                    isEnabled = category != it
                    setBackgroundColor(context.theme.getColorByAttr(if (isEnabled) R.attr.colorBackground else R.attr.colorBackgroundHighlight))
                }
                vb.moneyEditText.onDone { categorizeAdvancedVM.userInputCA(category, it.toMoneyBigDecimal()) }
                amountFormula.observe(lifecycle) { _amountFormula ->
                    vb.tvPercentage.easyVisibility = _amountFormula is AmountFormula.Percentage
                    getView()?.requestFocus() // required for onDone to not accidentally capture the new text.
                    vb.moneyEditText.setText(_amountFormula.toDisplayStr())
                    vb.moneyEditText.setOnCreateContextMenuListener { menu, _, _ ->
                        menu.add(
                            *listOfNotNull(
                                if (_amountFormula !is AmountFormula.Percentage)
                                    MenuItem(
                                        title = "Percentage",
                                        onClick = { categorizeAdvancedVM.userSwitchCategoryIsPercentage(category, true) })
                                else null,
                                if (_amountFormula !is AmountFormula.Value)
                                    MenuItem(
                                        title = "No Percentage",
                                        onClick = { categorizeAdvancedVM.userSwitchCategoryIsPercentage(category, false) })
                                else null,
                            ).toTypedArray()
                        )
                    }
                }
            }
        )
        val checkboxRecipeFactory = ViewItemRecipeFactory3<ItemCheckboxBinding, Category>(
            { ItemCheckboxBinding.inflate(LayoutInflater.from(requireContext())) },
            { category, vb, lifecycle ->
                vb.checkbox.bind(categorizeAdvancedVM.autoFillCategory, lifecycle) {
                    isChecked = category == it
                    isEnabled = category != it
                }
                vb.checkbox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) categorizeAdvancedVM.userSetCategoryForAutoFill(category)
                }
            }
        )
        categorizeAdvancedVM.categoryAmountFormulasToShow
            .map { categoryAmountFormulasToShow ->
                val recipes2D =
                    listOf(
                        listOf(
                            recipeFactories.header.createOne("Category"),
                            recipeFactories.header.createOne("Amount"),
                            recipeFactories.header.createOne("Fill"),
                        ),
                        *categoryAmountFormulasToShow.map {
                            listOf(
                                recipeFactories.textView.createOne(it.key.name),
                                categoryAmountRecipeFactory.createOne(it),
                                checkboxRecipeFactory.createOne(it.key),
                            )
                        }.toTypedArray(),
                    )
                val dividerMap = categoryAmountFormulasToShow.keys
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to recipeFactories.titledDivider.createOne(it.value.type.name) }
                    .mapKeys { it.key + 1 } // header row
                Pair(recipes2D, dividerMap)
            }
            .observe(viewLifecycleOwner) { (recipeGrid, dividerMap) ->
                vb.tmTableViewCategoryAmounts.initialize(
                    recipeGrid = recipeGrid,
                    shouldFitItemWidthsInsideTable = true,
                    dividerMap = dividerMap,
                    rowFreezeCount = 1,
                )
            }

        // # Button RecyclerView
        categorizeAdvancedVM.replayOrFuture
            .observe(viewLifecycleOwner) { (replayOrFuture) ->
                vb.buttonsview.buttons = listOfNotNull(
                    if (categorizeAdvancedType == CategorizeAdvancedType.SPLIT)
                        ButtonVMItem(
                            title = "Save Replay",
                            onClick = {
                                if (categorizeAdvancedVM.areCurrentCAsValid.value!!) {
                                    val editText = EditText(requireContext())
                                    AlertDialog.Builder(requireContext())
                                        .setMessage("What would you like to name this replay?")
                                        .setView(editText)
                                        .setPositiveButton("Submit") { _, _ ->
                                            categorizeAdvancedVM.userSaveReplay(editText.easyText)
                                        }
                                        .setNegativeButton("Cancel") { _, _ -> }
                                        .show()
                                } else
                                    errorSubject.onNext(InvalidCategoryAmounts(""))
                            }
                        )
                    else null,
                    if (categorizeAdvancedType == CategorizeAdvancedType.CREATE_FUTURE)
                        ButtonVMItem(
                            title = "Save Future",
                            onClick = {
                                if (categorizeAdvancedVM.areCurrentCAsValid.value!!) {
                                    val editText = EditText(requireContext())
                                    AlertDialog.Builder(requireContext())
                                        .setMessage("What would you like to name this future?")
                                        .setView(editText)
                                        .setPositiveButton("Submit") { _, _ ->
                                            categorizeAdvancedVM.userSaveFuture(editText.easyText)
                                        }
                                        .setNegativeButton("Cancel") { _, _ -> }
                                        .show()
                                } else
                                    errorSubject.onNext(InvalidCategoryAmounts(""))
                            }
                        )
                    else null,
                    if (replayOrFuture is IReplay)
                        ButtonVMItem(
                            title = "Delete Replay",
                            onClick = {
                                AlertDialog.Builder(requireContext())
                                    .setMessage("Do you really want to delete this replay?")
                                    .setPositiveButton("Yes") { _, _ ->
                                        categorizeAdvancedVM.userDeleteReplay(replayOrFuture.name)
                                    }
                                    .setNegativeButton("No") { _, _ -> }
                                    .show()
                            }
                        )
                    else null,
                    if (categorizeAdvancedType != CategorizeAdvancedType.CREATE_FUTURE)
                        ButtonVMItem(
                            title = "Submit",
                            onClick = {
                                categorizeAdvancedVM.userSubmitCategorization()
                            }
                        )
                    else null,
                ).reversed()
            }
    }

    enum class Key { CategorizeAdvancedType }
    enum class CategorizeAdvancedType { SPLIT, CREATE_FUTURE, EDIT }
    companion object {
        private var _setupArgs: Triple<Transaction?, IReplayOrFuture?, CategorySelectionVM>? = null
        fun navTo(
            source: Any,
            nav: NavController,
            categorySelectionVM: CategorySelectionVM,
            transaction: Transaction?,
            replayOrFuture: IReplayOrFuture?,
            categorizeAdvancedType: CategorizeAdvancedType
        ) {
            _setupArgs = Triple(
                transaction,
                replayOrFuture,
                categorySelectionVM
            )
            nav.navigate(
                when (source) {
                    is CategorizeFrag -> R.id.action_categorizeFrag_to_categorizeAdvancedFrag
                    else -> R.id.categorizeAdvancedFrag
                },
                Bundle().apply { putInt(Key.CategorizeAdvancedType.name, categorizeAdvancedType.ordinal) }
            )
        }
    }
}