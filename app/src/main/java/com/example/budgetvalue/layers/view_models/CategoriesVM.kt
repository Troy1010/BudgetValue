package com.example.budgetvalue.layers.view_models

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.models.Category
import com.example.budgetvalue.models.CategoryTypes
import com.example.budgetvalue.util.ObservableArrayList
import com.example.budgetvalue.util.ObservableHashMap
import com.example.budgetvalue.util.toBehaviorSubject
import com.example.tmcommonkotlin.logz
import io.reactivex.rxjava3.internal.operators.observable.ObservableMap
import io.reactivex.subjects.BehaviorSubject

class CategoriesVM : ViewModel() {
    val defaultCategory = Category("Default", CategoryTypes.Default)
    val incomeCategory = Category("Income", CategoryTypes.Income)
    val userAddedCategories = ObservableArrayList<Category>()
    val categories = userAddedCategories.observable.map {
        ArrayList(userAddedCategories + defaultCategory + incomeCategory)
    }.toBehaviorSubject()
    val choosableCategories = userAddedCategories.observable.map {
        ArrayList(userAddedCategories + incomeCategory)
    }.toBehaviorSubject()
    private val nameToCategoryMap = categories.map {
        it.associateBy { it.name } as HashMap<String, Category>
    }.toBehaviorSubject()
    fun getCategoryByName(name: String): Category {
        return nameToCategoryMap.value[name]!!
    }
    init {
        userAddedCategories.addAll(listOf(
            Category("Food", CategoryTypes.Always),
            Category("Soda", CategoryTypes.Always),
            Category("Vanity Food", CategoryTypes.Always),
            Category("Improvements", CategoryTypes.Always),
            Category("Dentist", CategoryTypes.Always),
            Category("Diabetic Supplies", CategoryTypes.Always),
            Category("Leli gifts/activities", CategoryTypes.Always)
        ))
    }
}
