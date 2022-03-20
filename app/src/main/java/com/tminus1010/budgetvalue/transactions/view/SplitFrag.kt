package com.tminus1010.budgetvalue.transactions.view

import android.app.AlertDialog
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.all_features.all_layers.InvalidCategoryAmounts
import com.tminus1010.budgetvalue.all_features.all_layers.InvalidSearchText
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.easyText
import com.tminus1010.budgetvalue.all_features.framework.view.recipe_factories.*
import com.tminus1010.budgetvalue.all_features.framework.view.viewBinding
import com.tminus1010.budgetvalue.databinding.FragCategorizeAdvancedBinding
import com.tminus1010.budgetvalue.transactions.app.Transaction
import com.tminus1010.budgetvalue.transactions.presentation.SplitVM
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.tuple.Box
import com.tminus1010.tmcommonkotlin.view.extensions.easyToast
import com.tminus1010.tmcommonkotlin.view.extensions.easyVisibility
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.subjects.Subject
import javax.inject.Inject

@AndroidEntryPoint
class SplitFrag : Fragment(R.layout.frag_categorize_advanced) {
    private val vb by viewBinding(FragCategorizeAdvancedBinding::bind)
    private val splitVM: SplitVM by viewModels()

    @Inject
    lateinit var errorSubject: Subject<Throwable>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Mediation
        _setupArgs?.also { _setupArgs = null; splitVM.setup(it.first) }
        // # Events
        splitVM.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        splitVM.saveReplayDialogBox.observe(viewLifecycleOwner) {
            if (splitVM.areCurrentCAsValid.value) {
                val editText = EditText(requireContext())
                editText.easyText = it
                AlertDialog.Builder(requireContext())
                    .setMessage("What would you like to name this replay?")
                    .setView(editText)
                    .setPositiveButton("Submit") { _, _ -> splitVM.userSaveReplay(editText.easyText) }
                    .setNegativeButton("Cancel") { _, _ -> }
                    .show()
            } else
                errorSubject.onNext(InvalidCategoryAmounts(""))
        }
        errorSubject.observe(viewLifecycleOwner) {
            when (it) {
                is InvalidCategoryAmounts -> easyToast("Invalid category amounts")
                is InvalidSearchText -> easyToast("Invalid search text")
                is SQLiteConstraintException -> easyToast("Invalid duplicate name")
                else -> throw it
            }
        }
        // # State
        vb.buttonsview.buttons = splitVM.buttons
        vb.tvAmountToSplit.bind(splitVM.amountToCategorizeMsg) { (it) -> easyVisibility = it != null; text = it }
        // ## TMTableView CategoryAmounts
        splitVM.categoryAmountFormulaVMItems
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
                                itemAmountFormulaRF().create(it, splitVM.fillCategory, { getView()?.requestFocus() }, it.menuVMItems),
                                itemCheckboxRF().create(it.isFillCategory, it.category.name, splitVM::userSetFillCategory),
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
    }

    companion object {
        private var _setupArgs: Box<Transaction>? = null
        fun navTo(
            nav: NavController,
            transaction: Transaction,
        ) {
            _setupArgs = Box(transaction)
            nav.navigate(R.id.splitFrag)
        }
    }
}