package com.tminus1010.buva.ui.set_string

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.KEY2
import com.tminus1010.buva.environment.ParcelableLambdaWrapper
import com.tminus1010.buva.ui.all_features.Navigator
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.EditTextVMItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class SetStringVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val navigator: Navigator,
) : ViewModel() {
    // # User Intents
    fun userSubmit() {
        savedStateHandle.get<ParcelableLambdaWrapper>(KEY2)!!.lambda2(currentS ?: "")
        navigator.navUp()
    }

    fun userSubmitFirstWord() {
        savedStateHandle.get<ParcelableLambdaWrapper>(KEY2)!!.lambda2(currentS?.split(Regex("""\s"""))?.firstOrNull() ?: "")
        navigator.navUp()
    }

    fun userSubmitFirst2Words() {
        savedStateHandle.get<ParcelableLambdaWrapper>(KEY2)!!.lambda2(currentS?.split(Regex("""\s"""))?.take(2)?.joinToString(" ") ?: "")
        navigator.navUp()
    }

    fun userSubmitFirst3Words() {
        savedStateHandle.get<ParcelableLambdaWrapper>(KEY2)!!.lambda2(currentS?.split(Regex("""\s"""))?.take(3)?.joinToString(" ") ?: "")
        navigator.navUp()
    }

    fun userCancel() {
        savedStateHandle.get<ParcelableLambdaWrapper>(KEY2)!!.lambda2(null)
        navigator.navUp()
    }

    // # Internal
    private var currentS: String? = savedStateHandle[KEY1]

    // # State
    val editTextVMItem =
        EditTextVMItem(
            text = savedStateHandle[KEY1],
            onDone = { currentS = it }
        )
    val buttons =
        flowOf(
            listOf(
                ButtonVMItem(
                    title = "Submit first three words",
                    onClick = ::userSubmitFirst3Words,
                ),
                ButtonVMItem(
                    title = "Submit first two words",
                    onClick = ::userSubmitFirst2Words,
                ),
                ButtonVMItem(
                    title = "Submit first word",
                    onClick = ::userSubmitFirstWord,
                ),
                ButtonVMItem(
                    title = "Submit",
                    onClick = ::userSubmit,
                ),
            )
        )
}