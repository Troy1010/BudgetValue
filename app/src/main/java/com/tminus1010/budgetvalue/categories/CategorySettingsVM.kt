package com.tminus1010.budgetvalue.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.InvalidCategoryNameException
import com.tminus1010.budgetvalue._core.all.extensions.nonLazyCache
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue.categories.data.CategoriesRepo2
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.categories.domain.DeleteCategoryFromActiveDomainUC
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.categories.models.CategoryType
import com.tminus1010.budgetvalue.transactions.app.AmountFormula
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.tuple.Box
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.rx3.asObservable
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CategorySettingsVM @Inject constructor(
    private val deleteCategoryFromActiveDomainUC: DeleteCategoryFromActiveDomainUC,
    private val categoriesRepo: CategoriesRepo2,
    private val errorSubject: Subject<Throwable>,
) : ViewModel() {
    // # Input
    // if categoryName is null, we are making a new category
    fun setup(categoryName: String?) {
        if (categoryName == null)
            _categoryToPush.onNext(Category(""))
        else
            categoriesRepo.userCategories
                .take(1)
                .asObservable()
                .map { it.find { it.name == categoryName }!! }
                .observe(disposables) { _categoryToPush.onNext(it) }
    }

    fun userSetName(categoryName: String) {
        if (categoryName != _categoryToPush.value!!.name)
            _categoryToPush.onNext(categoryToPush.value!!.copy(name = categoryName))
    }

    private val userDefaultAmountFormulaValue = BehaviorSubject.create<BigDecimal>()
    fun userSetDefaultAmountFormulaValue(defaultAmountFormulaValue: BigDecimal) {
        userDefaultAmountFormulaValue.onNext(defaultAmountFormulaValue)
    }

    private val userDefaultAmountFormulaIsPercentage = BehaviorSubject.createDefault(false)
    fun userSetDefaultAmountFormulaIsPercentage(isPercentage: Boolean) {
        userDefaultAmountFormulaIsPercentage.onNext(isPercentage)
    }

    fun userSetType(type: CategoryType) {
        if (type != _categoryToPush.value!!.type)
            _categoryToPush.onNext(categoryToPush.value!!.copy(type = type))
    }

    fun userDeleteCategory() {
        deleteCategoryFromActiveDomainUC(categoryToPush.value!!)
            .subscribe()
    }

    fun userSaveCategory() {
        Completable.fromCallable {
            if (categoryToPush.value!!.name == "" ||
                categoryToPush.value!!.name.equals(CategoriesInteractor.defaultCategory.name, ignoreCase = true) ||
                categoryToPush.value!!.name.equals(CategoriesInteractor.unrecognizedCategory.name, ignoreCase = true)
            ) throw InvalidCategoryNameException()
        }
            .andThen(Rx.fromSuspend<Boolean> { categoriesRepo.hasCategory(categoryToPush.value!!.name) })
            .flatMapCompletable {
                if (it)
                    Rx.completableFromSuspend { categoriesRepo.update(categoryToPush.value!!) }
                else
                    Rx.completableFromSuspend { categoriesRepo.push(categoryToPush.value!!) }
            }
            .subscribeBy(
                onComplete = { navigateUp.onNext(Unit) },
                onError = { errorSubject.onNext(it) },
            )
    }

    // # Output
    private val _categoryToPush = BehaviorSubject.create<Category>()
    val navigateUp: PublishSubject<Unit> = PublishSubject.create()
    val categoryToPush: Observable<Category> =
        Observable.combineLatest(
            _categoryToPush,
            userDefaultAmountFormulaValue.map { Box(it) }.startWithItem(Box(null)),
            userDefaultAmountFormulaIsPercentage.map { Box(it) }.startWithItem(Box(null)),
        )
        { categoryToPush, (amountFormulaValue), (amountFormulaIsPercentage) ->
            val defaultAmountFormula =
                if (amountFormulaValue == null || amountFormulaIsPercentage == null)
                    null
                else
                    (if (amountFormulaIsPercentage) AmountFormula.Percentage(amountFormulaValue) else AmountFormula.Value(amountFormulaValue))
            if (defaultAmountFormula == null)
                categoryToPush
            else
                categoryToPush.copy(defaultAmountFormula = defaultAmountFormula)
        }
            .nonLazyCache(disposables)
}