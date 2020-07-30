package com.example.budgetvalue.dependency_injection

import android.app.Application
import android.content.Context
import com.example.budgetvalue.layers.data_layer.Repo
import com.example.budgetvalue.layers.data_layer.TransactionParser
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {
    @Provides
    @Singleton
    fun providesAppContext(): Context = app

    @Provides
    @Singleton
    fun providesApp(): Application = app

    @Provides
    @Singleton
    fun providesRepo(): Repo {
        return Repo(
            TransactionParser()
        )
    }
}