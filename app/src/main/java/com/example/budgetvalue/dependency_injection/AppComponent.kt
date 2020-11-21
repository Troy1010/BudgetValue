package com.example.budgetvalue.dependency_injection

import com.example.budgetvalue.layer_data.Repo
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules= [AppModule::class, DBModule::class])
interface AppComponent {
    fun getRepo(): Repo
}