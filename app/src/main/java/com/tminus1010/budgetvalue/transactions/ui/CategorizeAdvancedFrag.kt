package com.tminus1010.budgetvalue.transactions.ui

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.InvalidCategoryAmounts
import com.tminus1010.budgetvalue._core.InvalidSearchText
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.extensions.easyText
import com.tminus1010.budgetvalue._core.extensions.easyVisibility
import com.tminus1010.budgetvalue._core.middleware.ui.recipe_factories.*
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.databinding.FragCategorizeAdvancedBinding
import com.tminus1010.budgetvalue.replay_or_future.models.IReplayOrFuture
import com.tminus1010.budgetvalue.transactions.CategorizeAdvancedVM
import com.tminus1010.budgetvalue.transactions.models.Transaction
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.view.extensions.easyToast
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.subjects.Subject
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
        _setupArgs?.also { _setupArgs = null; categorizeAdvancedVM.setup(it.first, it.second, it.third, categorizeAdvancedType) }
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
                is InvalidCategoryAmounts -> easyToast("Invalid category amounts")
                is InvalidSearchText -> easyToast("Invalid search text")
                is SQLiteConstraintException -> easyToast("Invalid duplicate name")
                else -> throw it
            }
        }
        categorizeAdvancedVM.deleteReplayDialogBox.observe(viewLifecycleOwner) {
            AlertDialog.Builder(requireContext())
                .setMessage("Do you really want to delete this replay?")
                .setPositiveButton("Yes") { _, _ -> categorizeAdvancedVM.userDeleteReplay(categorizeAdvancedVM.replayOrFuture.value?.first?.name ?: "") }
                .setNegativeButton("No") { _, _ -> }
                .show()
        }
        categorizeAdvancedVM.saveReplayDialogBox.observe(viewLifecycleOwner) {
            if (categorizeAdvancedVM.areCurrentCAsValid.value) {
                val editText = EditText(requireContext())
                AlertDialog.Builder(requireContext())
                    .setMessage("What would you like to name this replay?")
                    .setView(editText)
                    .setPositiveButton("Submit") { _, _ -> categorizeAdvancedVM.userSaveReplay(editText.easyText) }
                    .setNegativeButton("Cancel") { _, _ -> }
                    .show()
            } else
                errorSubject.onNext(InvalidCategoryAmounts(""))
        }

        // # TMTableView CategoryAmounts
        categorizeAdvancedVM.categoryAmountFormulaVMItems
            .map { categoryAmountFormulaVMItems ->
                val recipes2D =
                    listOf(
                        listOf(
                            itemHeaderRF().create("Category"),
                            itemHeaderRF().create("Amount"),
                            itemHeaderRF().create("Fill"),
                        ),
                        *categoryAmountFormulaVMItems.map {
                            listOf(
                                itemTextViewRB().create(it.category.name),
                                itemAmountFormulaRF().create(it, categorizeAdvancedVM.fillCategory, { getView()?.requestFocus() }, it.menuVMItems),
                                itemCheckboxRF().create(it.isFillCategory, it.category.name, categorizeAdvancedVM::userSetFillCategory),
                            )
                        }.toTypedArray(),
                    )
                val dividerMap = categoryAmountFormulaVMItems.map { it.category }
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { (k, v) -> k to itemTitledDividerRB().create(v.type.name) }
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
        vb.buttonsview.buttons = categorizeAdvancedVM.buttons
    }

    enum class Key { CategorizeAdvancedType }
    enum class CategorizeAdvancedType { SPLIT, EDIT }
    companion object {
        private var _setupArgs: Triple<Transaction?, IReplayOrFuture?, CategorySelectionVM>? = null
        fun navTo(
            source: Any,
            nav: NavController,
            categorySelectionVM: CategorySelectionVM,
            transaction: Transaction?,
            replayOrFuture: IReplayOrFuture?,
            categorizeAdvancedType: CategorizeAdvancedType,
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