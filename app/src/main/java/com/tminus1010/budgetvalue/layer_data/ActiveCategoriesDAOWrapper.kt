package com.tminus1010.budgetvalue.layer_data

import com.tminus1010.budgetvalue.categoryComparator
import com.tminus1010.budgetvalue.model_app.ICategoryParser
import com.tminus1010.budgetvalue.model_data.Category
import com.tminus1010.tmcommonkotlin.logz.logz
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

class ActiveCategoriesDAOWrapper @Inject constructor(
    val activeCategoriesDAO: ActiveCategoriesDAO,
) : IActiveCategoriesDAOWrapper,
    ICategoryParser,
    ActiveCategoriesDAO by activeCategoriesDAO {
    override val defaultCategory = Category("Default", Category.Type.Default, true)

    private val activeCategories_: Observable<List<Category>> =
        fetchActiveCategories()

    // activeCategories needs to start with an empty list, but
    // nameToCategoryMap must not start with an empty list.
    override val activeCategories: BehaviorSubject<List<Category>> =
        activeCategories_
            .map { it.sortedWith(categoryComparator) }
            .toBehaviorSubject(emptyList())

    override val categories = activeCategories
        .map { it + defaultCategory }
        .map { it.sortedWith(categoryComparator) }
        .toBehaviorSubject()

    private val nameToCategoryMap = activeCategories_
        .map { it.associateBy { it.name } as HashMap<String, Category> }
        .replay(1).apply { connect() }

    override fun parseCategory(categoryName: String): Category {
        require(categoryName != defaultCategory.name) { "Should never have to parse \"${defaultCategory.name}\"" }
        return nameToCategoryMap.blockingFirst()[categoryName]
            .also { if (it == null) logz("parseCategory`WARNING:had to return default for category name:$categoryName") }
            ?: defaultCategory
    }
}