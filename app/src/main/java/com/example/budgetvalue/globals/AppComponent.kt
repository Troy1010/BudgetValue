package com.example.budgetvalue.globals

import com.example.budgetvalue.dependency_injection.AppComponent
import com.example.budgetvalue.dependency_injection.AppModule
import com.example.budgetvalue.dependency_injection.DBModule
import com.example.budgetvalue.dependency_injection.DaggerAppComponent

val appComponent: AppComponent by lazy {
    DaggerAppComponent.builder()
        .appModule(AppModule { app })
        .dBModule(DBModule())
        .build()
}