package com.tminus1010.budgetvalue._core_testing

import android.app.Application
import androidx.test.platform.app.InstrumentationRegistry


val app by lazy { InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application }
