package com.tminus1010.budgetvalue._shared.app_init.data

import io.reactivex.rxjava3.core.Completable

interface IAppInitRepo {
    fun fetchAppInitBool(): Boolean
    fun pushAppInitBool(appInitBool: Boolean): Completable
}