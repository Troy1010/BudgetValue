package com.example.budgetvalue.extensions

import android.view.LayoutInflater
import androidx.test.platform.app.InstrumentationRegistry
import com.example.budgetvalue.AppMock
import com.example.budgetvalue.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ViewKtTest {
    val app by lazy { InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as AppMock }

    @Test
    fun setWidth() {
        // # Given
        val view = LayoutInflater.from(app).inflate(R.layout.blank_view, null, false)
        assertNotEquals(50, view.layoutParams?.width)
        // # Stimulate
        view.setWidth(50)
        // # Verify
        assertEquals(50, view.layoutParams?.width)
    }
}