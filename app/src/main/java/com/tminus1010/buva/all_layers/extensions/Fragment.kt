package com.tminus1010.buva.all_layers.extensions

import androidx.fragment.app.Fragment
import com.tminus1010.buva.framework.android.ShowAlertDialog


val Fragment.showAlertDialog get() = ShowAlertDialog(requireActivity())