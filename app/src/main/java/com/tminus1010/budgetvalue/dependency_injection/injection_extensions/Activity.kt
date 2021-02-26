package com.tminus1010.budgetvalue.dependency_injection.injection_extensions

import androidx.appcompat.app.AppCompatActivity
import com.tminus1010.budgetvalue.App


val AppCompatActivity.app
    get() = application as App

val AppCompatActivity.appComponent
    get() = app.appComponent

val AppCompatActivity.repo
    get() = appComponent.getRepo()

val AppCompatActivity.domain
    get() = appComponent.getDomain()