package com.example.budgetvalue.layers.view_models

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.models.Category
import com.example.budgetvalue.models.CategoryTypes

class CategoriesVM: ViewModel() {
    val defaultCategory = Category("Default", CategoryTypes.Default)
    val incomeCategory = Category("Income", CategoryTypes.Income)
    var userAddedCategories = ArrayList<Category>()
    val categories
        get() = ArrayList(userAddedCategories + defaultCategory + incomeCategory)
    val choosableCategories
        get() = ArrayList(userAddedCategories + incomeCategory)
    init {
        userAddedCategories.add(Category("Food", CategoryTypes.Always))
        userAddedCategories.add(Category("Soda", CategoryTypes.Always))
        userAddedCategories.add(Category("Vanity Food", CategoryTypes.Always))
        userAddedCategories.add(Category("Improvements", CategoryTypes.Always))
        userAddedCategories.add(Category("Dentist", CategoryTypes.Always))
        userAddedCategories.add(Category("Diabetic Supplies", CategoryTypes.Always))
        userAddedCategories.add(Category("Leli gifts/activities", CategoryTypes.Always))
    }
}