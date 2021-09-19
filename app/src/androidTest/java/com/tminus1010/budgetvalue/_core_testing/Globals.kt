package com.tminus1010.budgetvalue._core_testing

import android.app.Application
import androidx.test.platform.app.InstrumentationRegistry


val app get() = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application
