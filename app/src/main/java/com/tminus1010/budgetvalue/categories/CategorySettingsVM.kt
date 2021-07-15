package com.tminus1010.budgetvalue.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.InvalidCategoryNameException
import com.tminus1010.budgetvalue.categories.data.CategoriesRepo
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.categories.domain.DeleteCategoryFromActiveDomainUC
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.categories.models.CategoryType
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import javax.inject.Inject

@HiltViewModel
class CategorySettingsVM @Inject constructor(
    private val deleteCategoryFromActiveDomainUC: DeleteCategoryFromActiveDomainUC,
    private val categoriesRepo: CategoriesRepo,
    private val errorSubject: Subject<Throwable>,
) : ViewModel() {
    // # Input
    fun userSetName(categoryName: String) {
        if (categoryName != _categoryToPush.value!!.name)
            _categoryToPush.onNext(_categoryToPush.value!!.copy(name = categoryName))
    }

    fun userSetDefaultAmountFormula(defaultAmountFormula: AmountFormula) {
        if (defaultAmountFormula != _categoryToPush.value!!.defaultAmountFormula)
            _categoryToPush.onNext(_categoryToPush.value!!.copy(defaultAmountFormula = defaultAmountFormula))
    }

    fun userSetType(type: CategoryType) {
        if (type != _categoryToPush.value!!.type)
            _categoryToPush.onNext(_categoryToPush.value!!.copy(type = type))
    }

    fun userDeleteCategory() {
        deleteCategoryFromActiveDomainUC(_categoryToPush.value!!)
            .subscribe()
    }

    fun userSaveCategory() {
        Completable.fromCallable {
            if (categoryToPush.value!!.name == "" ||
                categoryToPush.value!!.name.equals(CategoriesDomain.defaultCategory.name, ignoreCase = true) ||
                categoryToPush.value!!.name.equals(CategoriesDomain.unknownCategory.name, ignoreCase = true)
            ) throw InvalidCategoryNameException()
        }
            .andThen(categoriesRepo.hasCategory(categoryToPush.value!!.name))
            .flatMapCompletable {
                if (it)
                    categoriesRepo.update(categoryToPush.value!!)
                else
                    categoriesRepo.push(categoryToPush.value!!)
            }
            .subscribeBy(
                onComplete = { navigateUp.onNext(Unit) },
                onError = { errorSubject.onNext(it) }
            )
    }

    // if categoryName is null, we are making a new category
    fun setup(categoryName: String?) {
        if (categoryName == null)
            _categoryToPush.onNext(Category(""))
        else
            categoriesRepo.userCategories
                .take(1)
                .map { it.find { it.name == categoryName }!! }
                .observe(disposables) { _categoryToPush.onNext(it) }
    }

    // # Internal
    private val _categoryToPush = BehaviorSubject.create<Category>()

    // # Output
    val navigateUp: PublishSubject<Unit> = PublishSubject.create()
    val categoryToPush: Observable<Category> = _categoryToPush
}