package com.tminus1010.buva.ui.import_and_categorize.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.buva.data.TransactionsRepo
import com.tminus1010.buva.environment.HostActivityWrapper
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@HiltViewModel
class ImportTransactionsVM @Inject constructor(
    private val hostActivityWrapper: HostActivityWrapper,
    private val transactionsRepo: TransactionsRepo,
) : ViewModel() {
    // # State
    val text =
        transactionsRepo.mostRecentImportItemDate
            .map {
                if (it == null)
                    NativeText.Simple("No imports have occurred yet.")
                else
                    NativeText.Simple("Most recent import item date: ${it.toDisplayStr()}")
            }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
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