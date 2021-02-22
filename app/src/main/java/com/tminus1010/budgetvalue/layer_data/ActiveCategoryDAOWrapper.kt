package com.tminus1010.budgetvalue.layer_data

import com.tminus1010.budgetvalue.categoryComparator
import com.tminus1010.budgetvalue.model_app.ICategoryParser
import com.tminus1010.budgetvalue.model_data.Category
import com.tminus1010.tmcommonkotlin.logz.logz
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

class ActiveCategoryDAOWrapper @Inject constructor(
    val activeCategoryDAO: ActiveCategoryDAO,
) : IActiveCategoryDAOWrapper,
    ICategoryParser,
    ActiveCategoryDAO by activeCategoryDAO {
    override val defaultCategory = Category("Default", Category.Type.Default, true)

    override val activeCategories: BehaviorSubject<List<Category>> =
        fetchActiveCategories()
            .map { it.sortedWith(categoryComparator) }
            .toBehaviorSubject()

    override val categories = activeCategories
        .map { it + defaultCategory }
        .map { it.sortedWith(categoryComparator) }
        .toBehaviorSubject()

    private val nameToCategoryMap = activeCategories
        .map { it.associateBy { it.name } as HashMap<String, Category> }
        .toBehaviorSubject()

    override fun parseCategory(categoryName: String): Category {
        val category = nameToCategoryMap.blockingFirst()[categoryName]
        if (category == null) logz("parseCategory`WARNING:had to return default for category name:$categoryName")
        return category ?: defaultCategory
    }
}