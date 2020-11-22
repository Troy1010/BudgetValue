package com.example.budgetvalue.dependency_injection

import android.content.Context
import android.content.SharedPreferences
import com.example.budgetvalue.App
import com.example.budgetvalue.SHARED_PREF_FILE_NAME
import com.example.budgetvalue.layer_data.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MiscModule(val app: App) {
    @Provides
    @Singleton
    fun providesApp(): App = app

    @Provides
    @Singleton
    fun providesSharedPreferences(app: App): SharedPreferences {
        return app.getSharedPreferences(
            SHARED_PREF_FILE_NAME,
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun providesDao(roomDatabase: BudgetValueDB): MyDao {
        return roomDatabase.myDao()
    }

    @Provides
    @Singleton
    fun providesRepo(myDao: MyDao, sharedPrefWrapper: SharedPrefWrapper): Repo {
        return Repo(
            TransactionParser(),
            sharedPrefWrapper,
            myDao
        )
    }
}