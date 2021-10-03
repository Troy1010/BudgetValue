package com.tminus1010.budgetvalue.categories.domain

import com.tminus1010.budgetvalue._core.categoryComparator
import com.tminus1010.budgetvalue.categories.ICategoryParser
import com.tminus1010.budgetvalue.categories.data.CategoriesRepo
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.categories.models.CategoryType
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoriesInteractor @Inject constructor(
    categoriesRepo: CategoriesRepo
) : ICategoryParser {
    // # Input
    override fun parseCategory(categoryName: String): Category {
        if (categoryName == defaultCategory.name) error("Should never have to parse \"${defaultCategory.name}\"")
        return nameToCategoryMap.blockingFirst()[categoryName]
            ?: unrecognizedCategory.also { logz("Warning: returning category Unrecognized for unrecognized name:$categoryName") }
    }

    // # Output
    val userCategories: BehaviorSubject<List<Category>> =
        categoriesRepo.userCategories
            .map { it.sortedWith(categoryComparator) }
            .toBehaviorSubject(emptyList())

    val categories: BehaviorSubject<List<Category>> =
        userCategories
            .map { it + defaultCategory + unrecognizedCategory }
            .map { it.sortedWith(categoryComparator) }
            .toBehaviorSubject()

    private val nameToCategoryMap =
        userCategories
            .skip(1)
            .map { it.associateBy { it.name } as HashMap<String, Category> }
            .replay(1).apply { connect() }

    companion object {
        val defaultCategory = Category("Default", CategoryType.Special, AmountFormula.Value(BigDecimal.ZERO), true)
        val unrecognizedCategory = Category("Unrecognized", CategoryType.Special, AmountFormula.Value(BigDecimal.ZERO), true)
    }
}