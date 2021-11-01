package com.tminus1010.budgetvalue.categories.data

import androidx.room.Room
import com.tminus1010.budgetvalue.__core_testing.app
import com.tminus1010.budgetvalue._core.data.CategoryDatabase
import com.tminus1010.budgetvalue._core.data.RoomTypeConverter
import com.tminus1010.budgetvalue._core.data.RoomWithCategoriesTypeConverter
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.categories.models.CategoryType
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class CategoriesRepoTest {
    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @Inject
    lateinit var roomTypeConverter: RoomTypeConverter

    lateinit var categoriesRepo: CategoriesRepo

    @Before
    fun before() {
        hiltAndroidRule.inject()
        categoriesRepo =
            CategoriesRepo(
                Room.inMemoryDatabaseBuilder(
                    app,
                    CategoryDatabase::class.java,
                )
                    .addTypeConverter(roomTypeConverter)
                    .build()
            )
    }

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
}