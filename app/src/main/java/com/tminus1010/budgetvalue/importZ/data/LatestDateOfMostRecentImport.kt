package com.tminus1010.budgetvalue.importZ.data

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.squareup.moshi.Moshi
import com.tminus1010.budgetvalue._core.all.extensions.mapBox
import com.tminus1010.budgetvalue._core.data.dataStore
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx3.asObservable
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LatestDateOfMostRecentImport @Inject constructor(
    private val app: Application,
    private val moshi: Moshi,
) : Observable<Box<LocalDate?>>() {
    private val key = stringPreferencesKey("LatestDateOfMostRecentImport")

    fun set(localDate: LocalDate) {
        GlobalScope.launch { app.dataStore.edit { it[key] = moshi.toJson(localDate) } }
    }

    private val latestDateOfMostRecentImport = app.dataStore.data.asObservable().mapBox { moshi.fromJson<LocalDate>(it[key]) }
    override fun subscribeActual(observer: Observer<in Box<LocalDate?>>?) = latestDateOfMostRecentImport.subscribe(observer)
}