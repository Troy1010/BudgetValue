package com.tminus1010.budgetvalue.aa_core.ui

import androidx.fragment.app.Fragment

interface IHostFragChild {
    val Fragment.hostFrag get() = (requireActivity() as HostActivity).hostFrag
}