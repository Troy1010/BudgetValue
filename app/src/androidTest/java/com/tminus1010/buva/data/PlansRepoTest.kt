package com.tminus1010.buva.data

import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.tminus1010.buva.core_testing.shared.FakeDataStore
import com.tminus1010.buva.core_testing.shared.Given
import com.tminus1010.buva.core_testing.app
import com.tminus1010.buva.environment.EnvironmentModule
import com.tminus1010.buva.all_layers.extensions.value
import com.tminus1010.buva.app.DatePeriodService
import com.tminus1010.buva.environment.CategoryDatabase
import com.tminus1010.buva.environment.MiscDatabase
import com.tminus1010.buva.environment.RoomWithCategoriesTypeConverter
import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.buva.domain.Plan
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
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@UninstallModules(EnvironmentModule::class)
@HiltAndroidTest
class PlansRepoTest {
    @Test
    fun default() = runBlocking {
        // # Given
        Given.categories.forEach { categoriesRepo.push(it) }
        // # When
        val result = plansRepo.plans.value
        // # Then
        assertEquals(listOf<Plan>(), result)
    }

    @Test
    fun push() = runBlocking {
        // # Given
        Given.categories.forEach { categoriesRepo.push(it) }
        val givenPlan =
            Plan(
                datePeriodService.getDatePeriod2(LocalDate.now()).first(),
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9"))
            )
        // # When
        plansRepo.push(givenPlan)
        Thread.sleep(1000)
        // # Then
        assertEquals(listOf(givenPlan), plansRepo.plans.value)
    }

    @Test
    fun updatePlanCategoryAmount() = runBlocking {
        // # Given
        Given.categories.forEach { categoriesRepo.push(it) }
        val givenPlan =
            Plan(
                datePeriodService.getDatePeriod2(LocalDate.now()).first(),
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9"))
            )
        plansRepo.push(givenPlan)
        Thread.sleep(1000)
        // # When
        plansRepo.updatePlanCategoryAmount(givenPlan, Given.categories[0], BigDecimal("35"))
        Thread.sleep(1000)
        // # Then
        assertEquals(
            listOf(
                Plan(
                    datePeriodService.getDatePeriod2(LocalDate.now()).first(),
                    BigDecimal("11"),
                    CategoryAmounts(Given.categories[0] to BigDecimal("35"))
                )
            ),
            plansRepo.plans.value,
        )
    }

    @Test
    fun updatePlanAmount() = runBlocking {
        // # Given
        Given.categories.forEach { categoriesRepo.push(it) }
        val givenPlan =
            Plan(
                datePeriodService.getDatePeriod2(LocalDate.now()).first(),
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9"))
            )
        plansRepo.push(givenPlan)
        Thread.sleep(1000)
        // # When
        plansRepo.updatePlanAmount(givenPlan, BigDecimal("77"))
        Thread.sleep(1000)
        // # Then
        assertEquals(
            listOf(
                Plan(
                    datePeriodService.getDatePeriod2(LocalDate.now()).first(),
                    BigDecimal("77"),
                    CategoryAmounts(Given.categories[0] to BigDecimal("9"))
                )
            ),
            plansRepo.plans.value,
        )
    }

    @Test
    fun updatePlan() = runBlocking {
        // # Given
        Given.categories.forEach { categoriesRepo.push(it) }
        val givenPlan =
            Plan(
                datePeriodService.getDatePeriod2(LocalDate.now()).first(),
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9"))
            )
        plansRepo.push(givenPlan)
        Thread.sleep(1000)
        val givenPlan2 =
            Plan(
                datePeriodService.getDatePeriod2(LocalDate.now()).first(),
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9"))
            )
        // # When
        plansRepo.updatePlan(givenPlan2)
        Thread.sleep(1000)
        // # Then
        assertEquals(listOf(givenPlan2), plansRepo.plans.value)
    }

    @Test
    fun delete() = runBlocking {
        // # Given
        Given.categories.forEach { categoriesRepo.push(it) }
        val givenPlan =
            Plan(
                datePeriodService.getDatePeriod2(LocalDate.now()).first(),
                BigDecimal("11"),
                CategoryAmounts(Given.categories[0] to BigDecimal("9"))
            )
        plansRepo.push(givenPlan)
        Thread.sleep(1000)
        // # When
        plansRepo.delete(givenPlan)
        Thread.sleep(1000)
        // # Then
        assertEquals(listOf<Plan>(), plansRepo.plans.value)
    }

    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @Inject
    lateinit var plansRepo: PlansRepo

    @Inject
    lateinit var datePeriodService: DatePeriodService

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