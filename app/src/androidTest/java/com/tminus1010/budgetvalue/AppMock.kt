package com.tminus1010.budgetvalue

import com.tminus1010.budgetvalue.dependency_injection.MiscModule
import com.tminus1010.budgetvalue.dependency_injection.BudgetValueDBModuleMock
import com.tminus1010.budgetvalue.dependency_injection.DaggerAppComponentMock

class AppMock : App() {
    override val appComponent by lazy {
        DaggerAppComponentMock.builder()
            .miscModule(MiscModule(this))
            .budgetValueDBModuleMock(BudgetValueDBModuleMock())
            .build()
    }
}