package com.tminus1010.buva.data

import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.tminus1010.buva.app.DatePeriodService
import com.tminus1010.buva.core_testing.app
import com.tminus1010.buva.core_testing.shared.FakeDataStore
import com.tminus1010.buva.core_testing.shared.Given
import com.tminus1010.buva.domain.ActivePlan
import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.buva.environment.EnvironmentModule
import com.tminus1010.buva.environment.adapter.RoomWithCategoriesTypeConverter
import com.tminus1010.buva.environment.database_or_datastore_or_similar.CategoryDatabase
import com.tminus1010.buva.environment.database_or_datastore_or_similar.MiscDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.BindValue
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
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@UninstallModules(EnvironmentModule::class)
@HiltAndroidTest
class ActivePlanRepoTest {
    @Test
    fun default() = runBlocking {
        // # Given
        val expected =
            ActivePlan(
                BigDecimal("0"),
                CategoryAmounts(),
            )
        // # When
        // # Then
        assertEquals(expected, activePlanRepo.activePlan.first())
    }

    @Test
    fun clearCategoryAmounts() = runBlocking {
        // # Given
        val expected =
            ActivePlan(
                BigDecimal("0"),
                CategoryAmounts(),
            )
        Given.categories.forEach { categoryRepo.push(it) }
        activePlanRepo.updateCategoryAmount(Given.categories[0], BigDecimal("9"))
        Thread.sleep(500) // Why is this necessary..?
        // # When
        activePlanRepo.clearCategoryAmounts()
        Thread.sleep(500) // Why is this necessary..?
        // # Then
        assertEquals(expected, activePlanRepo.activePlan.first())
    }

    @Test
    fun updateCategoryAmount() = runBlocking {
        // # Given
        val expected =
            ActivePlan(
                BigDecimal("0"),
                CategoryAmounts(Given.categories[0] to BigDecimal("22")),
            )
        Given.categories.forEach { categoryRepo.push(it) }
        // # When
        activePlanRepo.updateCategoryAmount(Given.categories[0], BigDecimal("22"))
        Thread.sleep(500) // Why is this necessary..?
        // # Then
        assertEquals(expected, activePlanRepo.activePlan.first())
    }

    @Test
    fun updateTotal() = runBlocking {
        // # Given
        val expected =
            ActivePlan(
                BigDecimal("98"),
                CategoryAmounts(),
            )
        // # When
        activePlanRepo.updateTotal(BigDecimal("98"))
        Thread.sleep(500) // Why is this necessary..?
        // # Then
        assertEquals(expected, activePlanRepo.activePlan.first())
    }

    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @Inject
    lateinit var datePeriodService: DatePeriodService

    @Inject
    lateinit var activePlanRepo: ActivePlanRepo

    @Inject
    lateinit var categoryRepo: CategoryRepo

    @Before
    fun before() {
        hiltAndroidRule.inject()
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