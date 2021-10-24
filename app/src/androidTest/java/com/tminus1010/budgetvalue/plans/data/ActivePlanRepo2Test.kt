package com.tminus1010.budgetvalue.plans.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tminus1010.budgetvalue.Given2
import com.tminus1010.budgetvalue.__core_testing.DatastoreInMemory
import com.tminus1010.budgetvalue._core.all.dependency_injection.MiscModule
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.categories.ICategoryParser
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.tmcommonkotlin.core.logx
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ActivePlanRepo2Test {
    lateinit var activePlanRepo: ActivePlanRepo2

    @Before
    fun before() {
        activePlanRepo = ActivePlanRepo2(
            DatastoreInMemory(),
            MiscModule.provideMoshi(),
            CategoryAmountsConverter(
                object : ICategoryParser {
                    override fun parseCategory(categoryName: String): Category {
                        return Given2.categories.find { it.name == categoryName }!!
                    }
                },
                MiscModule.provideMoshi()
            )
        )
    }

    @Test
    fun nullWhenNoCreate() {
        // # When
        activePlanRepo.activePlan
            .take(1)
            .test()
            .apply { await(3, TimeUnit.SECONDS) }
        // # Then
        assertNull(activePlanRepo.activePlan.value.logx("result"))
    }

    @Test
    fun create() {
        // # When
        activePlanRepo.create()
        activePlanRepo.activePlan
            .take(1)
            .test()
            .apply { await(5, TimeUnit.SECONDS) }
        // # Then
        assertNotNull(activePlanRepo.activePlan.value.logx("result"))
    }
}