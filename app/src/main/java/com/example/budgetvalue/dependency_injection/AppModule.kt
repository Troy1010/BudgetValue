package com.example.budgetvalue.dependency_injection

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.budgetvalue.SHARED_PREF_FILE_NAME
import com.example.budgetvalue.layer_data.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class AppModule(private val appProvider: () -> Application) {
    @Provides
    @Singleton
    open fun providesAppContext(): Context = appProvider()

    @Provides
    @Singleton
    open fun providesApp(): Application = appProvider()

    @Provides
    @Singleton
    open fun providesDatabase(app: Application): BudgetValueDB {
        return Room.databaseBuilder(app, BudgetValueDB::class.java, "BudgetValueDB")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    open fun providesSharedPreferences(appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences(
            SHARED_PREF_FILE_NAME,
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    open fun providesDao(roomDatabase: BudgetValueDB): MyDao {
        return roomDatabase.myDao()
    }

    @Provides
    @Singleton
    open fun providesRepo(myDao: MyDao, sharedPrefWrapper: SharedPrefWrapper): Repo {
        return Repo(
            TransactionParser(),
            sharedPrefWrapper,
            myDao
        )
    }
}