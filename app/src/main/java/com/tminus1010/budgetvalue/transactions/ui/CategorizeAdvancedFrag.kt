package com.tminus1010.budgetvalue.transactions.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.InvalidCategoryAmounts
import com.tminus1010.budgetvalue._core.extensions.add
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.extensions.easyText
import com.tminus1010.budgetvalue._core.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.middleware.ui.ButtonItem
import com.tminus1010.budgetvalue._core.middleware.ui.MenuItem
import com.tminus1010.budgetvalue._core.middleware.ui.onDone
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipeFactory3
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.recipeFactories
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.databinding.FragCategorizeAdvancedBinding
import com.tminus1010.budgetvalue.databinding.ItemCheckboxBinding
import com.tminus1010.budgetvalue.databinding.ItemMoneyEditTextBinding
import com.tminus1010.budgetvalue.replay.models.IReplay
import com.tminus1010.budgetvalue.transactions.CategorizeAdvancedVM
import com.tminus1010.budgetvalue.transactions.CategorizeVM
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
    private val categorizeVM: CategorizeVM by activityViewModels()
    private val categorizeAdvancedVM: CategorizeAdvancedVM by activityViewModels()
    private val replayName: String? by lazy { arguments?.getString(Key.REPLAY_NAME.name) }
    private var _shouldIgnoreUserInputForDuration = PublishSubject.create<Unit>()
    private var shouldIgnoreUserInput = _shouldIgnoreUserInputForDuration
        .flatMap { Observable.just(false).delay(1, TimeUnit.SECONDS).startWithItem(true) }
        .startWithItem(false)
        .replay(1).autoConnect()

    @Inject
    lateinit var errorSubject: Subject<Throwable>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Mediation
        _args?.also { _args = null; categorizeAdvancedVM.setup(it.first, it.second) }
        //
        shouldIgnoreUserInput.observe(viewLifecycleOwner) {}
        vb.tvTitle.text = if (replayName == null) "" else "Replay ($replayName)"
        vb.tvTitle.visibility = if (replayName == null) View.GONE else View.VISIBLE
        vb.tvAmountToSplit.bind(categorizeVM.amountToCategorize) { text = it }
        categorizeAdvancedVM.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        errorSubject.observe(viewLifecycleOwner) {
            if (it is InvalidCategoryAmounts)
                toast("Invalid category amounts")
            else
                throw it
        }
        // # TMTableView
        val categoryAmountRecipeFactory = ViewItemRecipeFactory3<ItemMoneyEditTextBinding, Map.Entry<Category, AmountFormula>>(
            { ItemMoneyEditTextBinding.inflate(LayoutInflater.from(context)) },
            { (category, amountFormula), vb, _ ->
                vb.editText.setText((amountFormula.amount + amountFormula.percentage).toString())
                vb.editText.onDone {
                    if (!shouldIgnoreUserInput.value!!)
                        categorizeAdvancedVM.userInputCA(category, it.toMoneyBigDecimal())
                }
                vb.editText.setOnCreateContextMenuListener { menu, _, _ ->
                    menu.add(
                        MenuItem(
                            title = "Fill",
                            onClick = {
                                _shouldIgnoreUserInputForDuration.onNext(Unit)
                                categorizeAdvancedVM.userFillIntoCategory(category)
                            }),
                        MenuItem(
                            title = "To Percentage",
                            onClick = {
                                _shouldIgnoreUserInputForDuration.onNext(Unit)
                                categorizeAdvancedVM.userSwitchCategoryToPercentage(category)
                            }),
                        MenuItem(
                            title = "To Non-Percentage",
                            onClick = {
                                _shouldIgnoreUserInputForDuration.onNext(Unit)
                                categorizeAdvancedVM.userSwitchCategoryToNonPercentage(category)
                            }),
                    )
                }
            }
        )
        val checkboxFactory = ViewItemRecipeFactory3<ItemCheckboxBinding, Category>(
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
        categorizeAdvancedVM.categoryAmountFormulas
            .map { categoryAmounts ->
                val recipes2D =
                    listOf(
                        listOf(
                            recipeFactories.header.createOne("Category"),
                            recipeFactories.header.createOne("Amount"),
                            recipeFactories.header.createOne("Fill"),
                        ),
                        listOf(
                            recipeFactories.textView.createOne("Default"),
                            recipeFactories.textViewWithLifecycle.createOne(categorizeAdvancedVM.defaultAmount),
                            checkboxFactory.createOne(CategoriesDomain.defaultCategory),
                        ),
                        *categoryAmounts.map {
                            listOf(
                                recipeFactories.textView.createOne(it.key.name),
                                categoryAmountRecipeFactory.createOne(it),
                                checkboxFactory.createOne(it.key),
                            )
                        }.toTypedArray(),
                    )
                val dividerMap = categoryAmounts.keys
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to recipeFactories.titledDivider.createOne(it.value.type.name) }
                    .mapKeys { it.key + 2 } // header row, and default row
                Pair(recipes2D, dividerMap)
            }
            .observe(viewLifecycleOwner) { (recipes2D, dividerMap) ->
                vb.tmTableView.initialize(
                    recipeGrid = recipes2D,
                    shouldFitItemWidthsInsideTable = true,
                    dividerMap = dividerMap,
                    rowFreezeCount = 1,
                )
            }

        // # Button RecyclerView
        vb.buttonsview.buttons = listOfNotNull(
            if (replayName == null)
                ButtonItem(
                    title = "Setup Auto Replay",
                    onClick = {
                        if (categorizeAdvancedVM.areCurrentCAsValid.value!!) {
                            val editText = EditText(requireContext())
                            AlertDialog.Builder(requireContext())
                                .setMessage("What would you like to name this replay?")
                                .setView(editText)
                                .setPositiveButton("Submit") { _, _ ->
                                    categorizeAdvancedVM.userSaveReplay(editText.easyText, true)
                                }
                                .setNegativeButton("Cancel") { _, _ -> }
                                .show()
                        } else
                            errorSubject.onNext(InvalidCategoryAmounts(""))
                    }
                )
            else null,
            if (replayName == null)
                ButtonItem(
                    title = "Save Replay",
                    onClick = {
                        if (categorizeAdvancedVM.areCurrentCAsValid.value!!) {
                            val editText = EditText(requireContext())
                            AlertDialog.Builder(requireContext())
                                .setMessage("What would you like to name this replay?")
                                .setView(editText)
                                .setPositiveButton("Submit") { _, _ ->
                                    categorizeAdvancedVM.userSaveReplay(editText.easyText, false)
                                }
                                .setNegativeButton("Cancel") { _, _ -> }
                                .show()
                        } else
                            errorSubject.onNext(InvalidCategoryAmounts(""))
                    }
                )
            else null,
            if (replayName != null)
                ButtonItem(
                    title = "Delete Replay",
                    onClick = {
                        AlertDialog.Builder(requireContext())
                            .setMessage("Do you really want to delete this replay?")
                            .setPositiveButton("Yes") { _, _ ->
                                categorizeAdvancedVM.userDeleteReplay(replayName!!)
                                nav.navigateUp()
                            }
                            .setNegativeButton("No") { _, _ -> }
                            .show()
                    }
                )
            else null,
            ButtonItem(
                title = "Submit",
                onClick = {
                    categorizeAdvancedVM.userSubmitCategorization()
                    nav.navigateUp()
                }
            ),
        ).reversed()
    }

    enum class Key { REPLAY_NAME }
    companion object {
        private var _args: Triple<Transaction, IReplay?, CategorySelectionVM>? = null
        fun navTo(
            source: Any,
            nav: NavController,
            categorySelectionVM: CategorySelectionVM,
            transaction: Transaction,
            replay: IReplay?,
        ) {
            _args = Triple(
                transaction,
                replay,
                categorySelectionVM
            )
            nav.navigate(
                when (source) {
                    is CategorizeFrag -> R.id.action_categorizeFrag_to_categorizeAdvancedFrag
                    else -> R.id.categorizeAdvancedFrag
                },
                Bundle().apply { putString(Key.REPLAY_NAME.name, replay?.name) }
            )
        }
    }
}