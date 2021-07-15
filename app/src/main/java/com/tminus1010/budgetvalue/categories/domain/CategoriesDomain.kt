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
class CategoriesDomain @Inject constructor(
    categoriesRepo: CategoriesRepo
) : ICategoryParser {
    // TODO: These can just be provided by CompanionObject
    val defaultCategory = CategoriesDomain.defaultCategory
    val unknownCategory = CategoriesDomain.unknownCategory

    val userCategories: BehaviorSubject<List<Category>> =
        categoriesRepo.userCategories
            .map { it.sortedWith(categoryComparator) }
            .toBehaviorSubject(emptyList())

    val categories: BehaviorSubject<List<Category>> =
        userCategories
            .map { it + defaultCategory + unknownCategory }
            .map { it.sortedWith(categoryComparator) }
            .toBehaviorSubject()

    val nameToCategoryMap =
        userCategories
            .skip(1)
            .map { it.associateBy { it.name } as HashMap<String, Category> }
            .replay(1).apply { connect() }

    override fun parseCategory(categoryName: String): Category {
        if (categoryName == defaultCategory.name) error("Should never have to parse \"${defaultCategory.name}\"")
        return nameToCategoryMap.blockingFirst()[categoryName]
            ?: unknownCategory.also { logz("Warning: returning category Unknown for unknown name:$categoryName") }
    }

    companion object {
        val defaultCategory = Category("Default", CategoryType.Special, AmountFormula.Value(BigDecimal.ZERO), true)
        val unknownCategory = Category("Unknown", CategoryType.Special, AmountFormula.Value(BigDecimal.ZERO), true)
    }
}