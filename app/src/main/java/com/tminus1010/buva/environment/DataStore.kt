package com.tminus1010.buva.environment

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore


val Context.dataStore by preferencesDataStore("dataStore")