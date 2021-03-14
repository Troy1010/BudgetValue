package com.tminus1010.budgetvalue.layer_domain

import com.tminus1010.budgetvalue.categoryComparator
import com.tminus1010.budgetvalue.layer_domain.use_cases.UserCategoriesUseCasesImpl
import com.tminus1010.budgetvalue.model_domain.Category
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject
import com.tminus1010.tmcommonkotlin.misc.logz

class UserCategories @Inject constructor(
    userCategoriesUseCasesImpl: UserCategoriesUseCasesImpl
) : ICategoryParser, IUserCategories {
    override val defaultCategory = Category("Default", Category.Type.Misc, true)
    override val unknownCategory = Category("Unknown", Category.Type.Misc, true)

    override val activeCategories: BehaviorSubject<List<Category>> =
        userCategoriesUseCasesImpl.fetchActiveCategories()
            .toBehaviorSubject(emptyList())

    override val categories: BehaviorSubject<List<Category>> =
        activeCategories
            .map { it + defaultCategory + unknownCategory }
            .map { it.sortedWith(categoryComparator) }
            .toBehaviorSubject()

    private val nameToCategoryMap =
        activeCategories
            .skip(1)
            .map { it.associateBy { it.name } as HashMap<String, Category> }
            .replay(1).apply { connect() }

    override fun parseCategory(categoryName: String): Category {
        if (categoryName == defaultCategory.name) error("Should never have to parse \"${defaultCategory.name}\"")
        return nameToCategoryMap.blockingFirst()[categoryName]
            ?: unknownCategory.also { logz("Warning: returning category Unknown for unknown name:$categoryName") }
    }
}