package com.example.budgetvalue.globals

import com.example.budgetvalue.dependency_injection.DaggerAppComponent
import com.example.budgetvalue.dependency_injection.MockAppModule

val appComponent by lazy {
    DaggerAppComponent.builder().appModule(MockAppModule { app }).build()
}