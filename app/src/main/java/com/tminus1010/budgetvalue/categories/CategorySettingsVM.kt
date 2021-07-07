package com.tminus1010.budgetvalue.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.extensions.nonLazyCache
import com.tminus1010.budgetvalue._core.extensions.unbox
import com.tminus1010.budgetvalue.categories.data.ICategoriesRepo
import com.tminus1010.budgetvalue.categories.domain.DeleteCategoryFromActiveDomainUC
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.tmcommonkotlin.core.logx
import com.tminus1010.tmcommonkotlin.tuple.Box
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CategorySettingsVM @Inject constructor(
    private val deleteCategoryFromActiveDomainUC: DeleteCategoryFromActiveDomainUC,
    private val categoriesRepo: ICategoriesRepo
) : ViewModel() {
    // # Input
    fun userUpdateDefaultAmount(defaultAmount: BigDecimal) {
        categoriesRepo.update(categoryBox.unbox.copy(defaultAmount = defaultAmount))
            .subscribe()
    }

    fun userDeleteCategory() {
        deleteCategoryFromActiveDomainUC(categoryBox.unbox)
            .subscribe()
    }

    fun setup(categoryName: String) {
        _categoryName.onNext(categoryName)
    }

    // # Output
    private val _categoryName = BehaviorSubject.create<String>()
    val categoryName: Observable<String> = _categoryName
    val categoryBox: Observable<Box<Category?>> =
        Observables.combineLatest(
            categoryName,
            categoriesRepo.fetchUserCategories()
        )
            .map { (categoryName, categories) -> Box(categories.find { it.name == categoryName }) }
            .nonLazyCache(disposables)
}