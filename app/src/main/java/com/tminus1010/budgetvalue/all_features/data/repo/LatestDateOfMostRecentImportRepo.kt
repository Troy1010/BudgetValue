package com.tminus1010.budgetvalue.all_features.data.repo

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.mapBox
import com.tminus1010.budgetvalue.all_features.data.MoshiProvider.moshi
import com.tminus1010.budgetvalue.all_features.data.dataStore
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

// TODO: migrate to flow
@Singleton
class LatestDateOfMostRecentImportRepo @Inject constructor(
    private val app: Application,
) : Observable<Box<LocalDate?>>() {
    private val key = stringPreferencesKey("LatestDateOfMostRecentImport")

    fun set(localDate: LocalDate) {
        GlobalScope.launch { app.dataStore.edit { it[key] = moshi.toJson(localDate) } }
    }

    private val latestDateOfMostRecentImport = app.dataStore.data.asObservable().mapBox { moshi.fromJson<LocalDate>(it[key]) }
    override fun subscribeActual(observer: Observer<in Box<LocalDate?>>?) = latestDateOfMostRecentImport.subscribe(observer)
}