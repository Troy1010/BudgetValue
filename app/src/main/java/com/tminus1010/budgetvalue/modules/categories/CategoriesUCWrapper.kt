package com.tminus1010.budgetvalue.modules.categories

import com.tminus1010.budgetvalue.categoryComparator
import com.tminus1010.tmcommonkotlin.misc.logz
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

class CategoriesUCWrapper @Inject constructor(
    userCategoriesUseCasesImpl: IUserCategoriesFetch
) : ICategoryParser, ICategories {
    override val defaultCategory = Category("Default", Category.Type.Misc, true)
    override val unknownCategory = Category("Unknown", Category.Type.Misc, true)

    override val userCategories: BehaviorSubject<List<Category>> =
        userCategoriesUseCasesImpl.fetchUserCategories()
            .toBehaviorSubject(emptyList())

    override val categories: BehaviorSubject<List<Category>> =
        userCategories
            .map { it + defaultCategory + unknownCategory }
            .map { it.sortedWith(categoryComparator) }
            .toBehaviorSubject()

    private val nameToCategoryMap =
        userCategories
            .skip(1)
            .map { it.associateBy { it.name } as HashMap<String, Category> }
            .replay(1).apply { connect() }

    override fun parseCategory(categoryName: String): Category {
        if (categoryName == defaultCategory.name) error("Should never have to parse \"${defaultCategory.name}\"")
        return nameToCategoryMap.blockingFirst()[categoryName]
            ?: unknownCategory.also { logz("Warning: returning category Unknown for unknown name:$categoryName") }
    }
}