package com.tminus1010.budgetvalue.layer_ui

import com.tminus1010.budgetvalue.categoryComparator
import com.tminus1010.budgetvalue.model_app.Category
import com.tminus1010.budgetvalue.source_objects.SourceArrayList
import com.tminus1010.budgetvalue.model_app.ICategoryParser
import com.tminus1010.tmcommonkotlin.logz.logz
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

/**
 * CategoriesAppVM is the viewModel for the categories themselves.
 * Because this is used by the repo, it must be an AppVM; ie - it must
 * be provided by dagger, not activityViewModels().
 */
class CategoriesAppVM : ICategoryParser {
    val defaultCategory = Category("Default", Category.Type.Default, true)
    val userAddedCategories = SourceArrayList<Category>()
    val categories = userAddedCategories.observable
        .map { ArrayList(userAddedCategories + defaultCategory) }
        .map { it.sortedWith(categoryComparator) }
        .toBehaviorSubject()
    val choosableCategories = userAddedCategories.observable
        .map { it.sortedWith(categoryComparator) }
        .toBehaviorSubject()
    private val nameToCategoryMap = categories
        .map { it.associateBy { it.name } as HashMap<String, Category> }
        .toBehaviorSubject()

    val intentDeleteCategory = PublishSubject.create<Category>()
        .also { it.subscribe { userAddedCategories.remove(it) } }

    init {
        userAddedCategories.addAll(listOf(
            Category("Food", Category.Type.Always),
            Category("Drinks", Category.Type.Always),
            Category("Improvements", Category.Type.Always),
            Category("Dentist", Category.Type.Always),
            Category("Diabetic Supplies", Category.Type.Always),
            Category("Leli gifts/activities", Category.Type.Always),
            Category("Misc", Category.Type.Always),
            Category("Gas", Category.Type.Always),
            Category("Vanity Food", Category.Type.Reservoir),
            Category("Emergency", Category.Type.Reservoir),
            Category("Charity", Category.Type.Reservoir),
            Category("Trips", Category.Type.Reservoir),
            Category("Christmas", Category.Type.Reservoir),
            Category("Gifts", Category.Type.Reservoir),
            Category("Activities", Category.Type.Reservoir),
            Category("CategoryA", Category.Type.Reservoir),
            Category("CategoryB", Category.Type.Reservoir),
            Category("CategoryC", Category.Type.Reservoir),
            Category("CategoryD", Category.Type.Reservoir),
            Category("CategoryE", Category.Type.Reservoir),
            Category("CategoryF", Category.Type.Reservoir),
            Category("CategoryG", Category.Type.Reservoir),
            Category("CategoryH", Category.Type.Reservoir),
        ))
    }

    override fun parseCategory(categoryName: String): Category {
        val category = nameToCategoryMap.value[categoryName]
        if (category == null) logz("parseCategory`WARNING:had to return default for category name:$categoryName")
        return category ?: defaultCategory
    }
}
