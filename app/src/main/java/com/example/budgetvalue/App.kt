package com.example.budgetvalue

import android.app.Application
import com.example.budgetvalue.dependency_injection.AppModule
import com.example.budgetvalue.dependency_injection.DaggerAppComponent
import com.tminus1010.tmcommonkotlin.logz.logz

class App : Application() {
    override fun onCreate() {
        logz("***************************START")
        super.onCreate()
    }

    val appComponent by lazy {
        DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }
}