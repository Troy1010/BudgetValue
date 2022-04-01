package com.tminus1010.budgetvalue.data

import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.tminus1010.budgetvalue.FakeDataStore
import com.tminus1010.budgetvalue.Given
import com.tminus1010.budgetvalue.__core_testing.app
import com.tminus1010.budgetvalue.all_layers.dependency_injection.EnvironmentModule
import com.tminus1010.budgetvalue.app.DatePeriodService
import com.tminus1010.budgetvalue.data.service.CategoryDatabase
import com.tminus1010.budgetvalue.data.service.MiscDatabase
import com.tminus1010.budgetvalue.data.service.RoomWithCategoriesTypeConverter
import com.tminus1010.budgetvalue.domain.ActivePlan
import com.tminus1010.budgetvalue.domain.CategoryAmounts
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
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@UninstallModules(EnvironmentModule::class)
@HiltAndroidTest
class ActivePlanRepoTest {
    @Test
    fun default() = runBlocking {
        // # Given
        // # When
        // # Then
        assertEquals(
            ActivePlan(
                BigDecimal("0"),
                CategoryAmounts(),
            ),
            activePlanRepo.activePlan.value
        )
        Thread.sleep(500) // Why is this necessary..?
    }

    @Test
    fun clearCategoryAmounts() = runBlocking {
        // # Given
        Given.categories.forEach { categoriesRepo.push(it) }
        activePlanRepo.updateCategoryAmount(Given.categories[0], BigDecimal("9"))
        Thread.sleep(500) // Why is this necessary..?
        // # When
        activePlanRepo.clearCategoryAmounts()
        Thread.sleep(500) // Why is this necessary..?
        // # Then
        assertEquals(
            ActivePlan(
                BigDecimal("0"),
                CategoryAmounts(),
            ),
            activePlanRepo.activePlan.value,
        )
        Thread.sleep(500) // Why is this necessary..?
    }

    @Test
    fun updateCategoryAmount() = runBlocking {
        // # Given
        Given.categories.forEach { categoriesRepo.push(it) }
        // # When
        activePlanRepo.updateCategoryAmount(Given.categories[0], BigDecimal("22"))
        Thread.sleep(500) // Why is this necessary..?
        // # Then
        assertEquals(
            ActivePlan(
                BigDecimal("0"),
                CategoryAmounts(Given.categories[0] to BigDecimal("22")),
            ),
            activePlanRepo.activePlan.value.logx("valueOfTest"),
        )
        Thread.sleep(500) // Why is this necessary..?
    }

    @Test
    fun updateTotal() = runBlocking {
        // # Given
        // # When
        activePlanRepo.updateTotal(BigDecimal("98"))
        Thread.sleep(500) // Why is this necessary..?
        // # Then
        assertEquals(
            ActivePlan(
                BigDecimal("98"),
                CategoryAmounts(),
            ),
            activePlanRepo.activePlan.value,
        )
        Thread.sleep(500) // Why is this necessary..?
    }

    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @Inject
    lateinit var datePeriodService: DatePeriodService

    @Inject
    lateinit var activePlanRepo: ActivePlanRepo

    @Inject
    lateinit var categoriesRepo: CategoriesRepo

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