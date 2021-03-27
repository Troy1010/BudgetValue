package com.tminus1010.budgetvalue.categories

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.aa_core.categoryComparator
import com.tminus1010.tmcommonkotlin.misc.logz
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

class CategoriesVM @Inject constructor(
    userCategoriesUseCasesImpl: IUserCategoriesFetch
) : ViewModel(), ICategoryParser {
    val defaultCategory = Category("Default", Category.Type.Misc, true)
    val unknownCategory = Category("Unknown", Category.Type.Misc, true)

    val userCategories: BehaviorSubject<List<Category>> =
        userCategoriesUseCasesImpl.fetchUserCategories()
            .map { it.sortedWith(categoryComparator) }
            .toBehaviorSubject(emptyList())

    val categories: BehaviorSubject<List<Category>> =
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