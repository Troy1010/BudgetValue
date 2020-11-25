package com.example.budgetvalue.layer_ui

import com.example.budgetvalue.model_app.Category
import com.example.budgetvalue.SourceArrayList
import com.example.budgetvalue.model_app.IParseCategory
import com.tminus1010.tmcommonkotlin.logz.logz
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject

/**
 * CategoriesAppVM is the viewModel for the categories themselves.
 * Because this is used by the repo, it must be an AppVM; ie - it must
 * be provided by dagger, not activityViewModels().
 */
class CategoriesAppVM: IParseCategory {
    val defaultCategory = Category("Default", Category.Type.Default)
    val incomeCategory = Category("Income", Category.Type.Income)
    val userAddedCategories = SourceArrayList<Category>()
    val categories = userAddedCategories.observable
        .map { ArrayList(userAddedCategories + defaultCategory + incomeCategory) }
        .toBehaviorSubject()
    val choosableCategories = userAddedCategories.observable
        .toBehaviorSubject()
    private val nameToCategoryMap = categories
        .map { it.associateBy { it.name } as HashMap<String, Category> }
        .toBehaviorSubject()

    fun getCategoryByName(name: String): Category {// TODO("Just combine with parseCategory")
        val category = nameToCategoryMap.value[name]
        if (category == null)
            logz("getCategoryByName`WARNING:had to return default for category name:$name")
        return category ?: defaultCategory
    }

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
            Category("Emergency", Category.Type.Reservoir)
        ))
    }

    override fun parseCategory(categoryName: String): Category {
        return getCategoryByName(categoryName)
    }
}
