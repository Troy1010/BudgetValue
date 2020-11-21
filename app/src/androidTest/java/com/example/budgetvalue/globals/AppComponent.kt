package com.example.budgetvalue.globals

import com.example.budgetvalue.dependency_injection.AppComponent
import com.example.budgetvalue.dependency_injection.AppModule
import com.example.budgetvalue.dependency_injection.DaggerAppComponentMock
import com.example.budgetvalue.dependency_injection.DBModuleMock

/**
 * This appComponent overrides the main/ source set's appComponent.
 */
val appComponent: AppComponent by lazy {
    DaggerAppComponentMock.builder()
        .appModule(AppModule { app })
        .dBModuleMock(DBModuleMock())
        .build()
}