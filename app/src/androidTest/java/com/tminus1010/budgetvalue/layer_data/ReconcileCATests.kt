package com.tminus1010.budgetvalue.layer_data

import androidx.test.platform.app.InstrumentationRegistry
import com.tminus1010.budgetvalue.AppMock
import com.tminus1010.budgetvalue.model_app.Category
import com.tminus1010.tmcommonkotlin.logz.logz
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.math.BigDecimal

class ReconcileCATests {
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
        repo.pushActiveReconcileCAs(mapOf(a to BigDecimal(8), b to BigDecimal(90), c to BigDecimal(3)))
        // # Stimulate
        repo.pushActiveReconcileCA(a to BigDecimal(123))
        // # Verify
        logz("uuu:${repo.fetchActiveReconcileCAs()}")
        assertEquals(BigDecimal(123), repo.fetchActiveReconcileCAs()[a])
        assertEquals(BigDecimal(90), repo.fetchActiveReconcileCAs()[b])
        assertEquals(BigDecimal(3), repo.fetchActiveReconcileCAs()[c])
    }
}