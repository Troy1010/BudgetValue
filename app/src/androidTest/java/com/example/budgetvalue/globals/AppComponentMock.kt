package com.example.budgetvalue.globals

import com.example.budgetvalue.dependency_injection.AppModule
import com.example.budgetvalue.dependency_injection.DaggerAppComponentMock
import com.example.budgetvalue.dependency_injection.BudgetValueDBModuleMock

val appComponentMock by lazy {
    DaggerAppComponentMock.builder()
        .appModule(AppModule { app })
        .budgetValueDBModuleMock(BudgetValueDBModuleMock())
        .build()
}