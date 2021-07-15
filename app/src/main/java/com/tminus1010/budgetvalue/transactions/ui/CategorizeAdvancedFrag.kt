package com.tminus1010.budgetvalue.transactions.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.InvalidCategoryAmounts
import com.tminus1010.budgetvalue._core.InvalidSearchText
import com.tminus1010.budgetvalue._core.extensions.*
import com.tminus1010.budgetvalue._core.middleware.ui.ButtonItem
import com.tminus1010.budgetvalue._core.middleware.ui.MenuItem
import com.tminus1010.budgetvalue._core.middleware.ui.onDone
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipe3
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipeFactory3
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.recipeFactories
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.databinding.*
import com.tminus1010.budgetvalue.replay.models.IReplay
import com.tminus1010.budgetvalue.replay.models.IReplayOrFuture
import com.tminus1010.budgetvalue.transactions.CategorizeAdvancedVM
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.budgetvalue.transactions.models.Transaction
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.toast
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class CategorizeAdvancedFrag : Fragment(R.layout.frag_categorize_advanced) {
    private val vb by viewBinding(FragCategorizeAdvancedBinding::bind)
    private val categorizeAdvancedVM: CategorizeAdvancedVM by viewModels()
    private var _shouldIgnoreUserInputForDuration = PublishSubject.create<Unit>()
    private var shouldIgnoreUserInput = _shouldIgnoreUserInputForDuration
        .flatMap { Observable.just(false).delay(1, TimeUnit.SECONDS).startWithItem(true) }
        .startWithItem(false)
        .replay(1).autoConnect()
    private val categorizeAdvancedType by lazy { CategorizeAdvancedType.values()[arguments?.getInt(Key.CategorizeAdvancedType.name)!!] }

    @Inject
    lateinit var errorSubject: Subject<Throwable>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Mediation
        _setupArgs?.also { _setupArgs = null; categorizeAdvancedVM.setup(it.first, it.second, it.third) }
        //
        shouldIgnoreUserInput.observe(viewLifecycleOwner) {}
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
        if (categorizeAdvancedType == CategorizeAdvancedType.CREATE_FUTURE)
            Observable.just(Unit)
                .map {
                    listOf(
                        listOf(
                            recipeFactories.textView.createOne("Search Text"),
                            searchTextRecipe,
                        ),
                        listOf(
                            recipeFactories.textView.createOne("Total Guess"),
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
        val categoryAmountRecipeFactory = ViewItemRecipeFactory3<ItemAmountFormulaBinding, Map.Entry<Category, AmountFormula>>(
            { ItemAmountFormulaBinding.inflate(LayoutInflater.from(context)) },
            { (category, amountFormula), vb, lifecycle ->
                vb.tvPercentage.easyVisibility = amountFormula is AmountFormula.Percentage
                vb.moneyEditText.bind(categorizeAdvancedVM.autoFillCategory, lifecycle) {
                    isEnabled = category != it
                    setBackgroundColor(context.theme.getColorByAttr(if (isEnabled) R.attr.colorBackground else R.attr.colorBackgroundHighlight))
                }
                vb.moneyEditText.setText(amountFormula.toDisplayStr())
                vb.moneyEditText.onDone {
                    if (!shouldIgnoreUserInput.value!!)
                        categorizeAdvancedVM.userInputCA(category, it.toMoneyBigDecimal())
                }
                vb.moneyEditText.setOnCreateContextMenuListener { menu, _, _ ->
                    menu.add(
                        *listOfNotNull(
                            MenuItem(
                                title = "Fill",
                                onClick = {
                                    _shouldIgnoreUserInputForDuration.onNext(Unit)
                                    categorizeAdvancedVM.userFillIntoCategory(category)
                                }),
                            if (amountFormula !is AmountFormula.Percentage)
                                MenuItem(
                                    title = "Percentage",
                                    onClick = {
                                        _shouldIgnoreUserInputForDuration.onNext(Unit)
                                        categorizeAdvancedVM.userSwitchCategoryIsPercentage(category, true)
                                    })
                            else null,
                            if (amountFormula !is AmountFormula.Value)
                                MenuItem(
                                    title = "No Percentage",
                                    onClick = {
                                        _shouldIgnoreUserInputForDuration.onNext(Unit)
                                        categorizeAdvancedVM.userSwitchCategoryIsPercentage(category, false)
                                    })
                            else null,
                        ).toTypedArray()
                    )
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
        val defaultAmountRecipe = ViewItemRecipe3<ItemTextViewBinding, Unit?>(
            { ItemTextViewBinding.inflate(LayoutInflater.from(requireContext())) },
            { _, vb, lifecycle ->
                vb.textview.bind(categorizeAdvancedVM.defaultAmount, lifecycle) { easyText = it }
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
                        listOf(
                            recipeFactories.textView.createOne("Default"),
                            defaultAmountRecipe,
                            checkboxRecipeFactory.createOne(CategoriesDomain.defaultCategory),
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
                    .mapKeys { it.key + 2 } // header row, and default row
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
                        ButtonItem(
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
                        ButtonItem(
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
                        ButtonItem(
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
                        ButtonItem(
                            title = "Submit",
                            onClick = {
                                categorizeAdvancedVM.userSubmitCategorization()
                                nav.navigateUp()
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