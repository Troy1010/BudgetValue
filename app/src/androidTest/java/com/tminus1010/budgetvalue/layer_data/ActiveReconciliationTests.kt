package com.tminus1010.budgetvalue.layer_data

import androidx.test.platform.app.InstrumentationRegistry
import com.tminus1010.budgetvalue.AppMock
import com.tminus1010.budgetvalue.model_data.Category
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.math.BigDecimal

class ActiveReconciliationTests {
    val app by lazy { InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as AppMock }
    val repo by lazy { app.appComponent.getRepo() }
    val a by lazy { Category("SomeCategoryA", Category.Type.Always) }
    val b by lazy { Category("SomeCategoryB", Category.Type.Always) }
    val c by lazy { Category("SomeCategoryC", Category.Type.Always) }
    val x by lazy {
        app.appComponent.getCategoriesAppVM()
            .userAddedCategories
            .addAll(listOf(
                a,
                b,
                c,
            ))
    }

    @Before
    fun before() {
        repo.pushActiveReconcileCAs(null)
        x
    }

    @Test
    fun pushActiveReconcileCA() {
        // # Given
        val input1 = mapOf(a to BigDecimal(8), b to BigDecimal(90), c to BigDecimal(3))
        val input2 = a to BigDecimal(123)
        // # Stimulate & Verify
        repo.pushActiveReconcileCAs(input1)
        assertEquals(BigDecimal(8), repo.fetchActiveReconcileCAs()[a])
        repo.pushActiveReconcileCA(input2)
        assertEquals(BigDecimal(123), repo.fetchActiveReconcileCAs()[a])
        assertEquals(BigDecimal(90), repo.fetchActiveReconcileCAs()[b])
        assertEquals(BigDecimal(3), repo.fetchActiveReconcileCAs()[c])
    }
}