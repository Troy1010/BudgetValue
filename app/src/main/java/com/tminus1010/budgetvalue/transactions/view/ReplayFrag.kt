package com.tminus1010.budgetvalue.transactions.view

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.InvalidCategoryAmounts
import com.tminus1010.budgetvalue._core.InvalidSearchText
import com.tminus1010.budgetvalue._core.all.extensions.bind
import com.tminus1010.budgetvalue._core.middleware.view.recipe_factories.*
import com.tminus1010.budgetvalue._core.middleware.view.viewBinding
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.databinding.FragCategorizeAdvancedBinding
import com.tminus1010.budgetvalue.replay_or_future.models.BasicReplay
import com.tminus1010.budgetvalue.transactions.presentation.ReplayVM
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.tuple.Box
import com.tminus1010.tmcommonkotlin.view.extensions.easyToast
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.subjects.Subject
import javax.inject.Inject

@AndroidEntryPoint
class ReplayFrag : Fragment(R.layout.frag_categorize_advanced) {
    private val vb by viewBinding(FragCategorizeAdvancedBinding::bind)
    private val replayVM by activityViewModels<ReplayVM>()
    private val categorySelectionVM: CategorySelectionVM by navGraphViewModels(R.id.categorizeNestedGraph) { defaultViewModelProviderFactory }

    @Inject
    lateinit var errorSubject: Subject<Throwable>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Mediation
        _setupArgs?.also { _setupArgs = null; replayVM.setup(it.first, categorySelectionVM) }
        // # Events
        replayVM.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        errorSubject.observe(viewLifecycleOwner) {
            when (it) {
                is InvalidCategoryAmounts -> easyToast("Invalid category amounts")
                is InvalidSearchText -> easyToast("Invalid search text")
                is SQLiteConstraintException -> easyToast("Invalid duplicate name")
                else -> throw it
            }
        }
        replayVM.navToSelectTransactionName.observe(viewLifecycleOwner) { nav.navigate(R.id.chooseTransactionDescriptionFrag2) }
        // # State
        vb.buttonsview.buttons = replayVM.buttons
        vb.tvTitle.easyVisibility = true
        vb.tvTitle.bind(replayVM.replay) { text = it.name }
        vb.tvAmountToSplit.bind(replayVM.amountOfSearchTexts) { text = it }
        replayVM.deleteReplayDialogBox.observe(viewLifecycleOwner) {
            AlertDialog.Builder(requireContext())
                .setMessage("Do you really want to delete this replay?")
                .setPositiveButton("Yes") { _, _ -> replayVM.userDeleteReplay(replayVM.replay.value?.name ?: "") }
                .setNegativeButton("No") { _, _ -> }
                .show()
        }

        // # TMTableView CategoryAmounts
        replayVM.categoryAmountFormulaVMItems
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
                                itemAmountFormulaRF().create(it, replayVM.fillCategory, { getView()?.requestFocus() }, it.menuVMItems),
                                itemCheckboxRF().create(it.isFillCategory, it.category.name, replayVM::userSetFillCategory),
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
        private var _setupArgs: Box<BasicReplay>? = null
        fun navTo(
            nav: NavController,
            replay: BasicReplay,
        ) {
            _setupArgs = Box(replay)
            nav.navigate(R.id.replayFrag)
        }
    }
}