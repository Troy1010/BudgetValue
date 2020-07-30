package com.example.budgetvalue.dependency_injection

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.budgetvalue.layers.data_layer.MyDao
import com.example.budgetvalue.layers.data_layer.BudgetValueDB
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
    fun providesDatabase(app: Application): BudgetValueDB {
        return Room.databaseBuilder(app, BudgetValueDB::class.java, "BudgetValueDB")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providesDao(roomDatabase: BudgetValueDB): MyDao {
        return roomDatabase.myDao()
    }

    @Provides
    @Singleton
    fun providesRepo(myDao: MyDao): Repo {
        return Repo(
            TransactionParser(),
            myDao
        )
    }
}