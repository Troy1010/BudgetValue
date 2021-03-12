package com.tminus1010.budgetvalue.dependency_injection.injection_extensions

import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.App
import com.tminus1010.tmcommonkotlin.view.extensions.nav


val Fragment.app
    get() = requireActivity().application as App

val Fragment.appComponent
    get() = app.appComponent

val Fragment.repo
    get() = appComponent.getRepo()

val Fragment.domain
    get() = appComponent.getDomain()

val Fragment.errorHandler
    get() = appComponent.getErrorHandler()

val Fragment.flavorIntersection
    get() = appComponent.getFlavorIntersection()

fun Fragment.handle(e: Throwable) =
    errorHandler.handle(nav, e)