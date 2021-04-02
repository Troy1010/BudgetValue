package com.tminus1010.budgetvalue._core

import android.app.Application
import com.tminus1010.budgetvalue._shared.app_init.AppInitDomain
import com.tminus1010.tmcommonkotlin.misc.logz
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import javax.inject.Inject

open class BaseApp : Application() {
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