package com.tminus1010.budgetvalue.data.service

import android.app.Application
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.tminus1010.budgetvalue.FakeDataStore
import com.tminus1010.budgetvalue.Given
import com.tminus1010.budgetvalue.__core_testing.app
import com.tminus1010.budgetvalue.all_layers.dependency_injection.EnvironmentModule
import com.tminus1010.budgetvalue.all_layers.dependency_injection.IEnvironmentModule
import com.tminus1010.budgetvalue.data.CategoriesRepo
import com.tminus1010.budgetvalue.domain.Transaction
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Singleton

@UninstallModules(EnvironmentModule::class)
@HiltAndroidTest
class MoshiWithCategoriesAdaptersTest {
    @Test
    fun toAndFromJson() {
        // # When
        val result =
            moshiWithCategoriesProvider.moshi.toJson(Given.transaction1)
                .logx("json")
                .let { moshiWithCategoriesProvider.moshi.fromJson<Transaction>(it) }
        // # Then
        assertEquals(Given.transaction1, result)
    }

    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @Inject
    lateinit var moshiWithCategoriesProvider: MoshiWithCategoriesProvider

    @Inject
    lateinit var categoriesRepo: CategoriesRepo

    @Before
    fun before() {
        hiltAndroidRule.inject()
        runBlocking { Given.categories.forEach { categoriesRepo.push(it) } }
        Thread.sleep(500)
    }

    @InstallIn(SingletonComponent::class)
    @Module
    object MockModule : IEnvironmentModule {
        @Provides
        @Singleton
        override fun providesSharedPreferences(application: Application): SharedPreferences {
            return super.providesSharedPreferences(application)
        }

        @Provides
        @Singleton
        fun categoryDatabase(): CategoryDatabase {
            return Room.inMemoryDatabaseBuilder(app, CategoryDatabase::class.java).build()
        }

        @Provides
        @Singleton
        fun miscDatabase(roomWithCategoriesTypeConverter: RoomWithCategoriesTypeConverter): MiscDatabase {
            return Room.inMemoryDatabaseBuilder(app, MiscDatabase::class.java)
                .addTypeConverter(roomWithCategoriesTypeConverter)
                .fallbackToDestructiveMigration()
                .build()
        }

        @Provides
        @Singleton
        fun dataStore(): DataStore<Preferences> {
            return FakeDataStore()
        }
    }
}