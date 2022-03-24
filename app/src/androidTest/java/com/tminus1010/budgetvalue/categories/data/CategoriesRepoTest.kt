package com.tminus1010.budgetvalue.categories.data

import android.app.Application
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.tminus1010.budgetvalue.__core_testing.app
import com.tminus1010.budgetvalue.all_features.all_layers.dependency_injection.EnvironmentModule
import com.tminus1010.budgetvalue.all_features.all_layers.dependency_injection.IEnvironmentModule
import com.tminus1010.budgetvalue.all_features.data.service.CategoryDatabase
import com.tminus1010.budgetvalue.all_features.data.service.MiscDatabase
import com.tminus1010.budgetvalue.all_features.data.service.RoomWithCategoriesTypeConverter
import com.tminus1010.budgetvalue.all_features.app.model.Category
import com.tminus1010.budgetvalue.all_features.app.model.CategoryType
import com.tminus1010.budgetvalue.all_features.data.repo.CategoriesRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Singleton

@UninstallModules(EnvironmentModule::class)
@HiltAndroidTest
class CategoriesRepoTest {
    @Test
    fun default() = runBlocking {
        // # When
        val result = categoriesRepo.userCategories.first()
        // # Then
        assertEquals(listOf<Category>(), result)
    }

    @Test
    fun push() = runBlocking {
        // # Given
        val givenCategory = Category("Given Category")
        // # When
        categoriesRepo.push(givenCategory)
        val result = categoriesRepo.userCategories.first()
        // # Then
        assertEquals(listOf(givenCategory), result)
    }

    @Test
    fun update() = runBlocking {
        // # Given
        val givenCategory = Category("Given Category")
        val givenNewType = CategoryType.Reservoir
        categoriesRepo.push(givenCategory)
        // # When
        categoriesRepo.update(givenCategory.copy(type = givenNewType))
        val result = categoriesRepo.userCategories.first().find { it.name == givenCategory.name }!!.type
        // # Then
        assertEquals(givenNewType, result)
    }

    @Test
    fun delete() = runBlocking {
        // # Given
        val givenCategory = Category("Given Category")
        categoriesRepo.push(givenCategory)
        // # When
        categoriesRepo.delete(givenCategory)
        val result = categoriesRepo.userCategories.first()
        // # Then
        assertEquals(listOf<Category>(), result)
    }

    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @Inject
    lateinit var categoriesRepo: CategoriesRepo

    @Before
    fun before() {
        hiltAndroidRule.inject()
    }

    @InstallIn(SingletonComponent::class)
    @Module
    object MockModule: IEnvironmentModule {
        @Provides
        @Singleton
        override fun providesSharedPreferences(application: Application): SharedPreferences {
            return super.providesSharedPreferences(application)
        }

        @Provides
        @Singleton
        override fun provideDataStore(application: Application): DataStore<Preferences> {
            return super.provideDataStore(application)
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
    }
}