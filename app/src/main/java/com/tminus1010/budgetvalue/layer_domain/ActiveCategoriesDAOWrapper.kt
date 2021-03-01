package com.tminus1010.budgetvalue.layer_domain

import com.tminus1010.budgetvalue.categoryComparator
import com.tminus1010.budgetvalue.layer_data.ActiveCategoriesDAO
import com.tminus1010.budgetvalue.model_domain.Category
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject
import com.tminus1010.tmcommonkotlin.logz.logz
import io.reactivex.rxjava3.core.Completable

class ActiveCategoriesDAOWrapper @Inject constructor(
    val activeCategoriesDAO: ActiveCategoriesDAO,
) : IActiveCategoriesDAOWrapper,
    ICategoryParser {
    override val defaultCategory = Category("Default", Category.Type.Misc, true)
    override val unknownCategory = Category("Unknown", Category.Type.Misc, true)

    // activeCategories needs to start with an empty list, but
    // nameToCategoryDTOMap must not start with an empty list.
    private val activeCategories_: BehaviorSubject<List<Category>> =
        activeCategoriesDAO.fetchActiveCategories()
            .map { it.map { Category.fromDTO(it) } }
            .toBehaviorSubject()

    override fun push(category: Category): Completable =
        activeCategoriesDAO.push(category.toDTO())

    override val activeCategories: BehaviorSubject<List<Category>> =
        activeCategories_
            .map { it.sortedWith(categoryComparator) }
            .toBehaviorSubject(emptyList())

    override val categories: BehaviorSubject<List<Category>> =
        activeCategories
            .map { it + defaultCategory + unknownCategory }
            .map { it.sortedWith(categoryComparator) }
            .toBehaviorSubject()

    private val nameToCategoryMap =
        activeCategories_
            .map { it.associateBy { it.name } as HashMap<String, Category> }
            .replay(1).apply { connect() }

    override fun parseCategory(categoryName: String): Category {
        require(categoryName != defaultCategory.name) { "Should never have to parse \"${defaultCategory.name}\"" }
        return nameToCategoryMap.blockingFirst()[categoryName]
            ?: unknownCategory.also { logz("Warning: returning category Unknown for unknown name:$categoryName") }
    }
}