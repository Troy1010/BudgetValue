package com.tminus1010.budgetvalue.framework.view

import android.app.Application
import android.widget.Toast
import com.tminus1010.budgetvalue.framework.isMainThread
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.Reusable
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

@Reusable
class ShowToast @Inject constructor(private val application: Application) {
    operator fun invoke(body: NativeText) {
        if (isMainThread)
            Toast.makeText(application, body.toCharSequence(application), Toast.LENGTH_SHORT).show()
        else
            Completable.fromCallable { Toast.makeText(application, body.toCharSequence(application), Toast.LENGTH_SHORT).show() }.subscribeOn(AndroidSchedulers.mainThread()).subscribe()
    }
}