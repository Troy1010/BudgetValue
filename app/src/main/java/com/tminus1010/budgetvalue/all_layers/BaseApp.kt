package com.tminus1010.budgetvalue.all_layers

import android.app.Application
import androidx.annotation.Keep
import io.reactivex.rxjava3.plugins.RxJavaPlugins

@Keep
open class BaseApp : Application() {
    override fun onCreate() {
        logz("!*!*! START")
        super.onCreate()

        // # Configure Rx
        RxJavaPlugins.setErrorHandler { throw it.cause ?: it }
    }
}