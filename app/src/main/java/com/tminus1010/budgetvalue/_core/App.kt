package com.tminus1010.budgetvalue._core

import android.app.Application
import com.tminus1010.budgetvalue._core.shared_features.app_init.AppInitDomain
import com.tminus1010.tmcommonkotlin.misc.logz
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import javax.inject.Inject

@HiltAndroidApp
open class App : Application() {
    @Inject lateinit var appInitDomain: AppInitDomain

    override fun onCreate() {
        logz("!*!*! START")
        super.onCreate()

        // # Configure Rx
        RxJavaPlugins.setErrorHandler { throw it.cause?:it }
        // # Initialize app once per install
        appInitDomain.appInit()
    }
}