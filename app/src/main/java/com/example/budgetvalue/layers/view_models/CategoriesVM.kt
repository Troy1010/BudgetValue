package com.example.budgetvalue.layers.view_models

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.models.Category
import com.example.budgetvalue.models.CategoryTypes
import com.example.budgetvalue.util.SourceArrayList
import com.example.budgetvalue.util.toBehaviorSubject
import com.example.tmcommonkotlin.logz

val categoriesVM by lazy { categoriesVM_ } // TODO: hacky
private lateinit var categoriesVM_: CategoriesVM
class CategoriesVM : ViewModel() {
    val defaultCategory = Category("Default", CategoryTypes.Default)
    val incomeCategory = Category("Income", CategoryTypes.Income)
    val userAddedCategories = SourceArrayList<Category>()
    val categories = userAddedCategories.observable.map {
        ArrayList(userAddedCategories + defaultCategory + incomeCategory)
    }.toBehaviorSubject()
    val choosableCategories = userAddedCategories.observable.toBehaviorSubject()
    private val nameToCategoryMap = categories.map {
        it.associateBy { it.name } as HashMap<String, Category>
    }.toBehaviorSubject()
    fun getCategoryByName(name: String): Category {
        val category = nameToCategoryMap.value[name]
        if (category==null)
            logz("getCategoryByName`WARNING:had to return default for category name:$name")
        return category?:defaultCategory
    }
    init {
        categoriesVM_ = this
        categoriesVM
        userAddedCategories.addAll(listOf(
            Category("Food", CategoryTypes.Always),
            Category("Drinks", CategoryTypes.Always),
            Category("Vanity Food", CategoryTypes.Always),
            Category("Improvements", CategoryTypes.Always),
            Category("Dentist", CategoryTypes.Always),
            Category("Diabetic Supplies", CategoryTypes.Always),
            Category("Leli gifts/activities", CategoryTypes.Always),
            Category("Misc", CategoryTypes.Always),
            Category("Gas", CategoryTypes.Always)
        ))
    }
}
