package com.tminus1010.budgetvalue

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class CustomTestRunner: AndroidJUnitRunner() {
    // # Use AppMock
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, AppMock::class.java.name, context)
    }
}