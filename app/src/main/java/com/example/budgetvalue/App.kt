package com.example.budgetvalue

import android.app.Application
import com.example.budgetvalue.dependency_injection.DaggerAppComponent
import com.example.budgetvalue.dependency_injection.MiscModule
import com.tminus1010.tmcommonkotlin.logz.logz

open class App : Application() {
    override fun onCreate() {
        logz("!*!*! START")
        super.onCreate()
    }
    open val appComponent by lazy {
        DaggerAppComponent.builder()
            .miscModule(MiscModule(this))
            .build()
    }
}