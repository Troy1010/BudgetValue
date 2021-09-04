package com.tminus1010.budgetvalue.replay_or_future

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.extensions.cold
import com.tminus1010.budgetvalue._core.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue._core.extensions.isZero
import com.tminus1010.budgetvalue._core.extensions.nonLazyCache
import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.budgetvalue._core.middleware.ui.ButtonVMItem
import com.tminus1010.budgetvalue._core.models.CategoryAmountFormulaVMItem
import com.tminus1010.budgetvalue._core.models.CategoryAmountFormulas
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.ICategoryParser
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.replay_or_future.data.FuturesRepo
import com.tminus1010.budgetvalue.replay_or_future.models.BasicFuture
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.budgetvalue.transactions.models.SearchType
import com.tminus1010.tmcommonkotlin.misc.generateUniqueID
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.SingleSubject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CreateFutureVM @Inject constructor(
    private val categoryParser: ICategoryParser,
    private val futuresRepo: FuturesRepo,
) : ViewModel() {
    // # Workarounds
    lateinit var categorySelectionVM: CategorySelectionVM
    fun setup(categorySelectionVM: CategorySelectionVM) {
        this.categorySelectionVM = categorySelectionVM
        categorySelectionVM.selectedCategories.observe(disposables) { selectedCategories.onSuccess(it) }
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
    fun userSwitchCategoryIsPercentage(category: Category, isPercentage: Boolean) {
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

    fun userSubmit() {
        futuresRepo.add(
            BasicFuture(
                name = generateUniqueID(),
                searchText = searchDescription.value,
                categoryAmountFormulas = categoryAmountFormulas.value,
                fillCategory = fillCategory.value,
                isPermanent = false,
            )
        )
            .andThen(categorySelectionVM.clearSelection())
            .andThen { navUp.onNext(Unit) }
            .subscribe()
    }

    // # Internal
    private val selectedCategories = SingleSubject.create<List<Category>>()
    private val userCategoryAmountFormulas =
        Observable.combineLatest(userCategoryAmounts.observable, userCategoryIsPercentage.observable)
        { userCategoryAmounts, userCategoryIsPercentage ->
            (userCategoryAmounts.keys + userCategoryIsPercentage.keys)
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
        Observable.combineLatest(userCategoryAmountFormulas, selectedCategories.toObservable())
        { userCategoryAmountFormulas, selectedCategories ->
            CategoryAmountFormulas(selectedCategories.associateWith { it.defaultAmountFormula })
                .plus(userCategoryAmountFormulas)
        }
            .nonLazyCache(disposables)
            .cold()

    // # Output
    val totalGuessHeader = "Total Guess"
    val totalGuess =
        userSetTotalGuess
            .startWithItem(BigDecimal.ZERO)
            .distinctUntilChanged()!!
    val searchTypeHeader = "Search Type"
    val searchType =
        userSetSearchType
            .startWithItem(SearchType.DESCRIPTION_AND_TOTAL)!!
    val searchDescriptionHeader = "Description"
    val searchDescription =
        userSetSearchDescription
            .startWithItem("")
            .distinctUntilChanged()
            .cold()
    val buttonVMItems =
        listOf(
            ButtonVMItem(
                "Submit",
                onClick = ::userSubmit
            )
        )
    val navUp = PublishSubject.create<Unit>()!!

    val fillCategory =
        userSetFillCategory
            .startWith(
                selectedCategories
                    .map { selectedCategories ->
                        selectedCategories.find { it.defaultAmountFormula.isZero() }
                            ?: selectedCategories.getOrNull(0)
                            ?: CategoriesDomain.defaultCategory
                    }
            )
            .distinctUntilChanged()
            .nonLazyCache(disposables)
            .cold()

    @VisibleForTesting
    val fillCategoryAmountFormula =
        Observable.combineLatest(categoryAmountFormulas, fillCategory, totalGuess)
        { categoryAmountFormulas, fillCategory, total ->
            Pair(fillCategory, categoryAmountFormulas.fillIntoCategory(fillCategory, total)[fillCategory]!!)
        }!!

    val categoryHeader = "Category"
    val amountHeader = "Amount"
    val fillHeader = "Fill"
    val categoryAmountFormulaVMItems: Observable<List<CategoryAmountFormulaVMItem>> =
        categoryAmountFormulas.flatMapSourceHashMap { it.itemObservableMap }
            .map { categoryAmountFormulaItemObservables ->
                categoryAmountFormulaItemObservables.map { (category, amountFormula) ->
                    CategoryAmountFormulaVMItem(category, amountFormula, fillCategoryAmountFormula)
                }
            }
            .nonLazyCache(disposables)
}