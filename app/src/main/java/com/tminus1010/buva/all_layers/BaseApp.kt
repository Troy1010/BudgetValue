package com.tminus1010.buva.all_layers

import android.app.Application
import androidx.annotation.Keep
import io.reactivex.rxjava3.plugins.RxJavaPlugins

@Keep
open class BaseApp : Application() {
    override fun onCreate() {
        logv("!*!*! START")
        super.onCreate()
        // # Configure Rx
        RxJavaPlugins.setErrorHandler { throw it.cause ?: it }
    }
}