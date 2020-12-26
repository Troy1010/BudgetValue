package com.tminus1010.budgetvalue

import android.app.Application
import com.tminus1010.budgetvalue.dependency_injection.DaggerAppComponent
import com.tminus1010.budgetvalue.dependency_injection.MiscModule
import com.tminus1010.tmcommonkotlin.logz.logz
import io.reactivex.rxjava3.plugins.RxJavaPlugins

open class App : Application() {
    override fun onCreate() {
        logz("!*!*! START")
        super.onCreate()

        // # Configure Rx
        RxJavaPlugins.setErrorHandler { throw it.cause?:it }
    }
    open val appComponent by lazy {
        DaggerAppComponent.builder()
            .miscModule(MiscModule(this))
            .build()
    }
}