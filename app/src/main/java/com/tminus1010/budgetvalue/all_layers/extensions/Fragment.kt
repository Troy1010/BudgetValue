package com.tminus1010.budgetvalue.all_layers.extensions

import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.framework.androidx.ShowAlertDialog


val Fragment.showAlertDialog get() = ShowAlertDialog(requireActivity())