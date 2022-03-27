package com.tminus1010.budgetvalue.ui.edit_string

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.ui.all_features.model.ButtonVMItem
import com.tminus1010.budgetvalue.ui.all_features.model.EditTextVMItem2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

@HiltViewModel
class EditStringVM @Inject constructor(
    private val editStringSharedVM: EditStringSharedVM,
) : ViewModel() {
    // # User Intents
    fun userSubmit() {
        editStringSharedVM.userSubmitString.onNext(latestS)
    }

    fun userCancel() {
        editStringSharedVM.userCancel.onNext()
    }

    // # Internal
    var latestS = editStringSharedVM.initialS

    // # Events
    val navUp =
        merge(
            editStringSharedVM.userSubmitString,
            editStringSharedVM.userCancel,
        )

    // # State
    val editTextVMItem =
        EditTextVMItem2(
            text = editStringSharedVM.initialS,
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