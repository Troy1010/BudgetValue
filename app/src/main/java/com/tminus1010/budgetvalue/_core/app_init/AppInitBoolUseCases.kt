package com.tminus1010.budgetvalue._core.app_init

import io.reactivex.rxjava3.core.Completable

interface AppInitBoolUseCases {
    fun fetchAppInitBool(): Boolean
    fun pushAppInitBool(boolean: Boolean = true): Completable
}