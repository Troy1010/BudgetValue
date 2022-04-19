package com.tminus1010.buva.data

import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.tminus1010.buva.FakeDataStore
import com.tminus1010.buva.Given
import com.tminus1010.buva.core_testing.app
import com.tminus1010.buva.all_layers.dependency_injection.EnvironmentModule
import com.tminus1010.buva.data.service.CategoryDatabase
import com.tminus1010.buva.data.service.MiscDatabase
import com.tminus1010.buva.data.service.MoshiWithCategoriesProvider
import com.tminus1010.buva.data.service.RoomWithCategoriesTypeConverter
import com.tminus1010.buva.domain.Transaction
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
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

    @BindValue
    val fakeDataStore: DataStore<Preferences> = FakeDataStore()

    @BindValue
    val realSharedPreferences: SharedPreferences = EnvironmentModule.providesSharedPreferences(app)

    @BindValue
    val categoryDatabase: CategoryDatabase = Room.inMemoryDatabaseBuilder(app, CategoryDatabase::class.java).build()

    @InstallIn(SingletonComponent::class)
    @Module
    object MockModule {
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