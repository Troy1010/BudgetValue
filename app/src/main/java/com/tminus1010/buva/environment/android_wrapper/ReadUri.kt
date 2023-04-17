package com.tminus1010.buva.environment.android_wrapper

import android.app.Application
import android.net.Uri
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class ReadUri @Inject constructor(
    private val app: Application,
) {
    operator fun invoke(uri: Uri): BufferedReader =
        BufferedReader(InputStreamReader(app.contentResolver.openInputStream(uri) ?: error("Could not find data at uri:$uri")))
}