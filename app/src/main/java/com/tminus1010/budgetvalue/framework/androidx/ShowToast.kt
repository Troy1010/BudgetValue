package com.tminus1010.budgetvalue.framework.androidx

import android.app.Application
import android.widget.Toast
import com.tminus1010.budgetvalue.framework.launchOnMainThread
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.Reusable
import javax.inject.Inject

@Reusable
class ShowToast @Inject constructor(private val application: Application) {
    operator fun invoke(body: NativeText) = launchOnMainThread {
        Toast.makeText(application, body.toCharSequence(application), Toast.LENGTH_SHORT).show()
    }
}