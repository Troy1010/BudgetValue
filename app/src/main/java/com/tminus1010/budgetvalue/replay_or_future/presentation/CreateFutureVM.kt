package com.tminus1010.budgetvalue.replay_or_future.presentation

import android.annotation.SuppressLint
import androidx.navigation.NavController
import com.tminus1010.budgetvalue._core.all.extensions.cold
import com.tminus1010.budgetvalue._core.framework.Rx
import com.tminus1010.budgetvalue._core.framework.view.Toaster
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue._core.presentation.model.MenuVMItem
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.choose_transaction_description.ChooseTransactionDescriptionFrag
import com.tminus1010.budgetvalue.replay_or_future.app.SelectCategoriesModel
import com.tminus1010.budgetvalue.replay_or_future.data.FuturesRepo
import com.tminus1010.budgetvalue.replay_or_future.domain.BasicFuture
import com.tminus1010.budgetvalue.replay_or_future.domain.TerminationStrategy
import com.tminus1010.budgetvalue.replay_or_future.domain.TotalFuture
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.budgetvalue.transactions.app.use_case.CategorizeAllMatchingUncategorizedTransactions
import com.tminus1010.budgetvalue.transactions.presentation.model.SearchType
import com.tminus1010.tmcommonkotlin.misc.generateUniqueID
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
@Deprecated("Use CreateFuture2VM")
class CreateFutureVM @Inject constructor(
    private val futuresRepo: FuturesRepo,
    private val transactionsInteractor: TransactionsInteractor,
    override val categoriesInteractor: CategoriesInteractor,
    private val toaster: Toaster,
    private val categorizeAllMatchingUncategorizedTransactions: CategorizeAllMatchingUncategorizedTransactions,
    override val selectCategoriesModel: SelectCategoriesModel,
) : CategoryAmountFormulaVMItemsBaseVM() {
    // # Workarounds
    lateinit var selfDestruct: () -> Unit
    fun setup(selfDestruct: () -> Unit) {
        this.selfDestruct = selfDestruct
    }

    // # Input
    private val userSetTotalGuess = BehaviorSubject.create<BigDecimal>()
    fun userSetTotalGuess(amount: String) {
        userSetTotalGuess.onNext(BigDecimal(amount).setScale(2))
    }

    private val userSetSearchType = BehaviorSubject.create<SearchType>()
    fun userSetSearchType(searchType: SearchType) {
        userSetSearchType.onNext(searchType)
    }

    private val userSetSearchDescription = BehaviorSubject.create<String>()
    fun userSetSearchDescription(searchDescription: String) {
        userSetSearchDescription.onNext(searchDescription)
    }

    private val userSetIsPermanent = BehaviorSubject.create<Boolean>()
    fun userSetIsPermanent(b: Boolean) {
        userSetIsPermanent.onNext(b)
    }

    @SuppressLint("VisibleForTests")
    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    fun userSubmit() {
        when (searchType.value) {
            SearchType.DESCRIPTION_AND_TOTAL ->
                TODO()
            SearchType.TOTAL ->
                TotalFuture(
                    name = generateUniqueID(),
                    searchTotal = _totalGuess.value,
                    categoryAmountFormulas = categoryAmountFormulas.value,
                    fillCategory = fillCategory.value.first!!,
                    terminationStrategy = if (isPermanent.value) TerminationStrategy.PERMANENT else TerminationStrategy.WAITING_FOR_MATCH,
                    isAutomatic = true,
                )
            SearchType.DESCRIPTION ->
                BasicFuture(
                    name = generateUniqueID(),
                    searchTexts = listOf(searchDescription.value),
                    categoryAmountFormulas = categoryAmountFormulas.value,
                    fillCategory = fillCategory.value.first!!,
                    terminationStrategy = if (isPermanent.value) TerminationStrategy.PERMANENT else TerminationStrategy.WAITING_FOR_MATCH,
                    isAutomatic = true,
                    totalGuess = totalGuess.value,
                )
        }
            .let { newFuture ->
                Rx.merge(
                    futuresRepo.add(newFuture),
                    if (newFuture.terminationStrategy == TerminationStrategy.PERMANENT) categorizeAllMatchingUncategorizedTransactions(newFuture).doOnSuccess { toaster.toast("$it transactions categorized") }.ignoreElement() else null,
                )
            }
            .andThen(Completable.fromAction { runBlocking { selectCategoriesModel.clearSelection() } })
            .andThen(Completable.fromAction { navUp.onNext(Unit); selfDestruct() })
            .subscribe()
    }

    // # Output
    override val _totalGuess =
        userSetTotalGuess
            .startWithItem(BigDecimal.ZERO)
            .distinctUntilChanged()
            .cold()
    val isPermanentHeader = "Is Permanent"
    val isPermanent =
        userSetIsPermanent
            .startWithItem(false)
            .distinctUntilChanged()
            .cold()
    val searchTypeHeader = "Search Type"
    val searchType =
        userSetSearchType
            .startWithItem(SearchType.DESCRIPTION)
            .cold()
    val searchDescriptionHeader = "Description"
    val searchDescription =
        userSetSearchDescription
            .startWithItem(transactionsInteractor.mostRecentUncategorizedSpend.value!!.first?.description ?: "")
            .distinctUntilChanged()
            .cold()
    val searchDescriptionMenuVMItems = listOf(
        MenuVMItem(
            title = "Copy selection from history",
            onClick = { navTo.onNext(ChooseTransactionDescriptionFrag.Companion::navTo) },
        )
    )
    val buttonVMItems =
        listOf(
            ButtonVMItem(
                "Submit",
                onClick = ::userSubmit
            )
        )
    val navUp = PublishSubject.create<Unit>()!!
    val navTo = PublishSubject.create<(NavController) -> Unit>()!!
}