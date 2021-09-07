package com.tminus1010.budgetvalue.replay_or_future

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import androidx.navigation.NavController
import com.tminus1010.budgetvalue._core.extensions.cold
import com.tminus1010.budgetvalue._core.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue._core.extensions.isZero
import com.tminus1010.budgetvalue._core.extensions.nonLazyCache
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.budgetvalue._core.middleware.ui.ButtonVMItem
import com.tminus1010.budgetvalue._core.middleware.ui.MenuVMItem
import com.tminus1010.budgetvalue._core.models.CategoryAmountFormulaVMItem
import com.tminus1010.budgetvalue._core.models.CategoryAmountFormulas
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.ICategoryParser
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.choose_transaction_description.ChooseTransactionDescriptionFrag
import com.tminus1010.budgetvalue.replay_or_future.data.FuturesRepo
import com.tminus1010.budgetvalue.replay_or_future.models.BasicFuture
import com.tminus1010.budgetvalue.replay_or_future.models.TerminationStatus
import com.tminus1010.budgetvalue.replay_or_future.models.TotalFuture
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.budgetvalue.transactions.models.SearchType
import com.tminus1010.tmcommonkotlin.misc.generateUniqueID
import com.tminus1010.tmcommonkotlin.rx.extensions.retryWithDelay
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class CreateFutureVM @Inject constructor(
    private val categoryParser: ICategoryParser,
    private val futuresRepo: FuturesRepo,
) : ViewModel() {
    // # Workarounds
    lateinit var categorySelectionVM: CategorySelectionVM
    lateinit var selfDestruct: () -> Unit
    fun setup(categorySelectionVM: CategorySelectionVM, selfDestruct: () -> Unit) {
        this.selfDestruct = selfDestruct
        this.categorySelectionVM = categorySelectionVM
    }

    // # Input
    private val userCategoryAmounts = SourceHashMap<Category, BigDecimal>()
    fun userInputCA(category: Category, amount: BigDecimal) {
        if (amount.isZero)
            userCategoryAmounts.remove(category)
        else
            userCategoryAmounts[category] = amount
    }

    private val userCategoryIsPercentage = SourceHashMap<Category, Boolean>()
    fun userSetCategoryIsPercentage(category: Category, isPercentage: Boolean) {
        userCategoryIsPercentage[category] = isPercentage
    }

    private val userSetFillCategory = BehaviorSubject.create<Category>()!!
    fun userSetFillCategory(categoryName: String) {
        userSetFillCategory.onNext(categoryParser.parseCategory(categoryName))
    }

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

    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    fun userSubmit() {
        futuresRepo.add(
            when (searchType.value) {
                SearchType.DESCRIPTION_AND_TOTAL ->
                    TODO()
                SearchType.TOTAL ->
                    TotalFuture(
                        name = generateUniqueID(),
                        searchTotal = totalGuess.value,
                        categoryAmountFormulas = categoryAmountFormulas.value,
                        fillCategory = fillCategory.value,
                        terminationStatus = if (isPermanent.value) TerminationStatus.PERMANENT else TerminationStatus.WAITING_FOR_MATCH,
                    )
                SearchType.DESCRIPTION ->
                    BasicFuture(
                        name = generateUniqueID(),
                        searchText = searchDescription.value,
                        categoryAmountFormulas = categoryAmountFormulas.value,
                        fillCategory = fillCategory.value,
                        terminationStatus = if (isPermanent.value) TerminationStatus.PERMANENT else TerminationStatus.WAITING_FOR_MATCH,
                    )
            }
        )
            .andThen(categorySelectionVM.clearSelection())
            .andThen(Completable.fromAction { navUp.onNext(Unit); selfDestruct() })
            .subscribe()
    }

    // # Internal
    private val selectedCategories =
        Observable.defer { categorySelectionVM.selectedCategories }
            .retryWithDelay(200, TimeUnit.MILLISECONDS, 99999)
    private val userCategoryAmountFormulas =
        Observable.combineLatest(userCategoryAmounts.observable, userCategoryIsPercentage.observable, selectedCategories)
        { userCategoryAmounts, userCategoryIsPercentage, selectedCategories ->
            (userCategoryAmounts.keys + userCategoryIsPercentage.keys)
                .filter { it in selectedCategories }
                .associateWith {
                    if (userCategoryIsPercentage[it] ?: false)
                        AmountFormula.Percentage(userCategoryAmounts[it] ?: BigDecimal.ZERO)
                    else
                        AmountFormula.Value(userCategoryAmounts[it] ?: BigDecimal.ZERO)
                }
        }
            .nonLazyCache(disposables)

    @VisibleForTesting
    val categoryAmountFormulas =
        Observable.combineLatest(userCategoryAmountFormulas, selectedCategories)
        { userCategoryAmountFormulas, selectedCategories ->
            CategoryAmountFormulas(selectedCategories.associateWith { it.defaultAmountFormula })
                .plus(userCategoryAmountFormulas)
        }
            .nonLazyCache(disposables)
            .cold()

    // # Output
    val isPermanentHeader = "Is Permanent"
    val isPermanent =
        userSetIsPermanent
            .startWithItem(false)
            .distinctUntilChanged()
            .cold()
    val totalGuessHeader = "Total Guess"
    val totalGuess =
        userSetTotalGuess
            .startWithItem(BigDecimal.ZERO)
            .distinctUntilChanged()
            .cold()
    val searchTypeHeader = "Search Type"
    val searchType =
        userSetSearchType
            .startWithItem(SearchType.TOTAL)!!
            .cold()
    val searchDescriptionHeader = "Description"
    val searchDescription =
        userSetSearchDescription
            .startWithItem("")
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

    val fillCategory =
        selectedCategories
            .map { selectedCategories ->
                selectedCategories.find { it.defaultAmountFormula.isZero() }
                    ?: selectedCategories.getOrNull(0)
                    ?: CategoriesDomain.defaultCategory
            }
            .switchMap { userSetFillCategory.startWithItem(it) }
            .distinctUntilChanged()
            .nonLazyCache(disposables)
            .cold()

    @VisibleForTesting
    val fillAmountFormula =
        Observable.combineLatest(categoryAmountFormulas, fillCategory, totalGuess)
        { categoryAmountFormulas, fillCategory, total ->
            categoryAmountFormulas.fillIntoCategory(fillCategory, total)[fillCategory]
                ?: AmountFormula.Value(BigDecimal.ZERO) // Occurs when fillCategory is defaultCategory
        }
            .cold()

    val categoryHeader = "Category"
    val amountHeader = "Amount"
    val fillHeader = "Fill"
    val categoryAmountFormulaVMItems: Observable<List<CategoryAmountFormulaVMItem>> =
        categoryAmountFormulas
            .flatMapSourceHashMap { it.itemObservableMap }
            .map { categoryAmountFormulaItemObservables ->
                categoryAmountFormulaItemObservables.map { (category, amountFormula) ->
                    CategoryAmountFormulaVMItem(category, amountFormula, fillCategory, fillAmountFormula, ::userSetCategoryIsPercentage, ::userInputCA)
                }
            }
            .nonLazyCache(disposables)
}