package com.tminus1010.budgetvalue.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.InvalidCategoryNameException
import com.tminus1010.budgetvalue._core.extensions.unbox
import com.tminus1010.budgetvalue.categories.data.ICategoriesRepo
import com.tminus1010.budgetvalue.categories.domain.CategoriesDomain
import com.tminus1010.budgetvalue.categories.domain.DeleteCategoryFromActiveDomainUC
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.categories.models.CategoryType
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.tuple.Box
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CategorySettingsVM @Inject constructor(
    private val deleteCategoryFromActiveDomainUC: DeleteCategoryFromActiveDomainUC,
    private val categoriesRepo: ICategoriesRepo,
    private val errorSubject: Subject<Throwable>,
) : ViewModel() {
    // # Input
    fun userSetName(categoryName: String) {
        if (categoryName != _categoryToPush.unbox.name)
            _categoryToPush.onNext(Box(_categoryToPush.unbox.copy(name = categoryName)))
    }

    fun userSetDefaultAmount(defaultAmount: BigDecimal) {
        if (defaultAmount != _categoryToPush.unbox.defaultAmount)
            _categoryToPush.onNext(Box(_categoryToPush.unbox.copy(defaultAmount = defaultAmount)))
    }

    fun userSetType(type: CategoryType) {
        if (type != _categoryToPush.unbox.type)
            _categoryToPush.onNext(Box(_categoryToPush.unbox.copy(type = type)))
    }

    fun userDeleteCategory() {
        deleteCategoryFromActiveDomainUC(_categoryToPush.unbox)
            .subscribe()
    }

    fun userSaveCategory() {
        Completable.fromCallable {
            if (categoryToPush.unbox.name == "" ||
                categoryToPush.unbox.name.equals(CategoriesDomain.defaultCategory.name, ignoreCase = true) ||
                categoryToPush.unbox.name.equals(CategoriesDomain.unknownCategory.name, ignoreCase = true)
            ) throw InvalidCategoryNameException()
        }
            .andThen(categoriesRepo.hasCategory(categoryToPush.unbox.name))
            .flatMapCompletable {
                if (it)
                    categoriesRepo.update(categoryToPush.unbox)
                else
                    categoriesRepo.push(categoryToPush.unbox)
            }
            .subscribeBy(
                onComplete = { navigateUp.onNext(Unit) },
                onError = { errorSubject.onNext(it) }
            )
    }

    // if categoryName is null, we are making a new category
    fun setup(categoryName: String?) {
        if (categoryName == null)
            _categoryToPush.onNext(Box(Category("")))
        else
            categoriesRepo.userCategories
                .take(1)
                .map { Box(it.find { it.name == categoryName }) }
                .observe(disposables) { _categoryToPush.onNext(it) }
    }

    // # Internal
    private val _categoryToPush = BehaviorSubject.create<Box<Category?>>()

    // # Output
    val navigateUp: PublishSubject<Unit> = PublishSubject.create()
    val categoryToPush: Observable<Box<Category?>> = _categoryToPush
}