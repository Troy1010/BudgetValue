package com.tminus1010.budgetvalue._core.middleware

import android.app.Application
import android.os.Looper
import com.tminus1010.tmcommonkotlin.view.extensions.easyToast
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Toaster @Inject constructor(private val application: Application) {
    fun toast(s: String) {
        if (Looper.myLooper() == Looper.getMainLooper())
            application.easyToast(s)
        else
            Completable.fromCallable { application.easyToast(s) }.subscribeOn(AndroidSchedulers.mainThread()).subscribe()
    }
}