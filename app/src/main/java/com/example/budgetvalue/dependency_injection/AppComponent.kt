package com.example.budgetvalue.dependency_injection

import com.example.budgetvalue.layers.data_layer.Repo
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules= arrayOf(AppModule::class))
interface AppComponent {
    fun getRepo(): Repo
}