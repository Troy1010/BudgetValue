package com.example.budgetvalue.globals

import com.example.budgetvalue.dependency_injection.AppModule
import com.example.budgetvalue.dependency_injection.DaggerMockAppComponent
import com.example.budgetvalue.dependency_injection.MockDBModule

val mockAppComponent by lazy {
    DaggerMockAppComponent.builder()
        .appModule(AppModule { app })
        .mockDBModule(MockDBModule())
        .build()
}