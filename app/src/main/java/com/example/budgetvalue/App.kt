package com.example.budgetvalue

import android.app.Application
import com.example.budgetvalue.dependency_injection.AppModule
import com.example.budgetvalue.dependency_injection.DaggerAppComponent

class App : Application() {
    val appComponent by lazy {
        DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }
}