package com.tminus1010.budgetvalue.ui.edit_string

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.ui.all_features.model.ButtonVMItem
import com.tminus1010.budgetvalue.ui.all_features.model.EditTextVMItem2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class EditStringVM @Inject constructor(
    private val editStringSharedVM: EditStringSharedVM,
) : ViewModel() {
    // # User Intents
    fun userSubmit() {
        editStringSharedVM.userSubmitString.onNext(latestS)
        navUp.onNext()
    }

    // # Internal
    var latestS = editStringSharedVM.initialS

    // # Events
    val navUp = MutableSharedFlow<Unit>()

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