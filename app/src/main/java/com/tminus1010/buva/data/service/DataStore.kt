package com.tminus1010.buva.data.service

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore


val Context.dataStore by preferencesDataStore("dataStore")