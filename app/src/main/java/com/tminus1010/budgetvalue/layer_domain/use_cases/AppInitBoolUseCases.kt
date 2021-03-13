package com.tminus1010.budgetvalue.layer_domain.use_cases

import io.reactivex.rxjava3.core.Completable

interface AppInitBoolUseCases {
    fun fetchAppInitBool(): Boolean
    fun pushAppInitBool(boolean: Boolean = true): Completable
}