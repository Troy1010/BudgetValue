package com.tminus1010.buva.ui.set_string

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.KEY2
import com.tminus1010.buva.environment.ParcelableLambdaWrapper
import com.tminus1010.buva.ui.all_features.Navigator
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.EditTextVMItem
import com.tminus1010.tmcommonkotlin.core.tryOrNull
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
        callback(currentS ?: "")
        navigator.navUp()
    }

    fun userSubmitFirstWord() {
        callback(firstWord)
        navigator.navUp()
    }

    fun userSubmitFirst2Words() {
        callback(firstTwoWords)
        navigator.navUp()
    }

    fun userSubmitFirst3Words() {
        callback(firstThreeWords)
        navigator.navUp()
    }

    fun userCancel() {
        callback(null)
        navigator.navUp()
    }

    // # Private
    private val callback = savedStateHandle.get<ParcelableLambdaWrapper>(KEY2)!!.lambda
    private val originalS = savedStateHandle.get<String>(KEY1)
    private val firstWord = tryOrNull { originalS?.split(Regex("""\s+"""))?.firstOrNull() }
    private val firstTwoWords = tryOrNull { originalS?.split(Regex("""\s+"""))?.take(2)?.joinToString(" ") }
    private val firstThreeWords = tryOrNull { originalS?.split(Regex("""\s+"""))?.take(3)?.joinToString(" ") }
    private var currentS: String? = originalS

    // # State
    val editTextVMItem =
        EditTextVMItem(
            text = originalS,
            onDone = { currentS = it }
        )
    val buttons =
        flowOf(
            listOfNotNull(
                if (firstThreeWords != null)
                    ButtonVMItem(
                        title = "Submit \"$firstThreeWords\"",
                        onClick = ::userSubmitFirst3Words,
                    ) else null,
                if (firstTwoWords != null)
                    ButtonVMItem(
                        title = "Submit \"$firstTwoWords\"",
                        onClick = ::userSubmitFirst2Words,
                    ) else null,
                if (firstWord != null)
                    ButtonVMItem(
                        title = "Submit \"$firstWord\"",
                        onClick = ::userSubmitFirstWord,
                    ) else null,
                ButtonVMItem(
                    title = "Submit",
                    onClick = ::userSubmit,
                ),
            )
        )
}