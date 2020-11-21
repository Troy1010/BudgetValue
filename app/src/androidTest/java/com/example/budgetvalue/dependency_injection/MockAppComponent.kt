package com.example.budgetvalue.dependency_injection

import com.example.budgetvalue.layer_data.Repo
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules= [AppModule::class, MockDBModule::class])
interface MockAppComponent {
    fun getRepo(): Repo
}