package com.example.budgetvalue

import android.app.Application
import com.example.budgetvalue.globals.app
import com.tminus1010.tmcommonkotlin.logz.logz

open class App : Application() {
    override fun onCreate() {
        logz("***************************START")
        super.onCreate()
        app = this
    }
}