package com.tminus1010.budgetvalue._core

import android.app.Application
import com.tminus1010.tmcommonkotlin.core.logz
import io.reactivex.rxjava3.plugins.RxJavaPlugins

open class BaseApp : Application() {
    override fun onCreate() {
        logz("!*!*! START")
        super.onCreate()

        // # Configure Rx
        RxJavaPlugins.setErrorHandler { throw it.cause ?: it }
    }
}