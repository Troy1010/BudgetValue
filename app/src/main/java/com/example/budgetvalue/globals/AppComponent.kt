package com.example.budgetvalue.globals

import com.example.budgetvalue.dependency_injection.AppModule
import com.example.budgetvalue.dependency_injection.BudgetValueDBModule
import com.example.budgetvalue.dependency_injection.DaggerAppComponent

val appComponent by lazy {
    DaggerAppComponent.builder()
        .appModule(AppModule { app })
        .budgetValueDBModule(BudgetValueDBModule())
        .build()
}