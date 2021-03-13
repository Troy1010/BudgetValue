package com.tminus1010.budgetvalue.layer_ui

import androidx.navigation.fragment.NavHostFragment
import com.tminus1010.budgetvalue.extensions.getBackStack

class HostFrag : NavHostFragment() {
    fun getBackStack() = childFragmentManager.getBackStack()
}