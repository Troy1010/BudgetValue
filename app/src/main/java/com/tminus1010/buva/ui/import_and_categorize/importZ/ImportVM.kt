package com.tminus1010.buva.ui.import_and_categorize.importZ

import androidx.lifecycle.ViewModel
import com.tminus1010.buva.environment.HostActivityWrapper
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class ImportVM @Inject constructor(
    private val hostActivityWrapper: HostActivityWrapper,
) : ViewModel() {
    // # State
    val buttons =
        flowOf(
            listOfNotNull(
                ButtonVMItem(
                    title = "Import",
                    onClick = hostActivityWrapper::launchChooseFile
                ),
            )
        )
}