package com.tminus1010.budgetvalue.all_features.all_layers.extensions

import android.annotation.SuppressLint
import androidx.navigation.NavController

@SuppressLint("RestrictedApi")
fun NavController.backstackNames() =
    backStack.map { it.destination.label }

@SuppressLint("RestrictedApi")
fun NavController.backstack() =
    backStack