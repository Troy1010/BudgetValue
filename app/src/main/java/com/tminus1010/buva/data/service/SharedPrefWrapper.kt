package com.tminus1010.buva.data.service

import android.content.SharedPreferences
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefWrapper @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) {
    companion object {
        enum class Key {
            APP_INIT_BOOL,
        }
    }

    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    // # AppInitBool

    fun isAppInitialized(): Boolean =
        sharedPreferences.getBoolean(Key.APP_INIT_BOOL.name, false)

    fun pushAppInitBool(boolean: Boolean): Completable {
        editor.putBoolean(Key.APP_INIT_BOOL.name, boolean)
        return Completable.fromAction { editor.commit() }
    }
}