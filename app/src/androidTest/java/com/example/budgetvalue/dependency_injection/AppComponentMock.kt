package com.example.budgetvalue.dependency_injection

import com.example.budgetvalue.layer_data.MyDao
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules= [AppModule::class, DBModuleMock::class])
interface AppComponentMock : AppComponent {
    fun getMyDao(): MyDao
}