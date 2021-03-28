package com.tminus1010.budgetvalue._core

import android.app.Application
import com.tminus1010.budgetvalue._core.dependency_injection.AppComponent
import com.tminus1010.budgetvalue._core.dependency_injection.DaggerAppComponent
import com.tminus1010.budgetvalue._core.dependency_injection.MiscModule
import com.tminus1010.tmcommonkotlin.misc.logz
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.rxjava3.plugins.RxJavaPlugins

@HiltAndroidApp
open class App : Application() {
    override fun onCreate() {
        logz("!*!*! START")
        super.onCreate()

        // # Configure Rx
        RxJavaPlugins.setErrorHandler { throw it.cause?:it }
        // # Initialize app once per install
        appComponent.getDomain().appInit()
    }
    open val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .miscModule(MiscModule(this))
            .build()
    }
}