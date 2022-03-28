package com.tminus1010.budgetvalue.ui.set_string

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.ui.all_features.model.ButtonVMItem
import com.tminus1010.budgetvalue.ui.all_features.model.EditTextVMItem2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

@HiltViewModel
class SetStringVM @Inject constructor(
    private val setStringSharedVM: SetStringSharedVM,
) : ViewModel() {
    // # User Intents
    fun userSubmit() {
        setStringSharedVM.userSubmitString.onNext(latestS)
    }

    fun userCancel() {
        setStringSharedVM.userCancel.onNext()
    }

    // # Internal
    var latestS = setStringSharedVM.initialS

    // # Events
    val navUp =
        merge(
            setStringSharedVM.userSubmitString,
            setStringSharedVM.userCancel,
        )

    // # State
    val editTextVMItem =
        EditTextVMItem2(
            text = setStringSharedVM.initialS,
            onDone = { latestS = it }
        )
    val buttons =
        flowOf(
            listOf(
                ButtonVMItem(
                    title = "Submit",
                    onClick = ::userSubmit,
                )
            )
        )
}