package com.example.budgetvalue

import com.example.budgetvalue.dependency_injection.AppModule
import com.example.budgetvalue.dependency_injection.BudgetValueDBModuleMock
import com.example.budgetvalue.dependency_injection.DaggerAppComponentMock

class AppMock : App() {
    override val appComponent by lazy {
        DaggerAppComponentMock.builder()
            .appModule(AppModule { this })
            .budgetValueDBModuleMock(BudgetValueDBModuleMock())
            .build()
    }
}