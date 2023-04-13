package com.tminus1010.buva.ui.importZ

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.data.SelectedImportHostPage
import com.tminus1010.buva.ui.importZ.categorize.CategorizeFrag
import com.tminus1010.buva.ui.importZ.transactions.AccountsFrag
import com.tminus1010.buva.ui.importZ.transactions.ImportTransactionsFrag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ImportHostVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val selectedImportHostPage: SelectedImportHostPage,
) : ViewModel() {
    // # User Intent
    fun userSelectMenuItem(id: Int) {
        selectedImportHostPage.set(id)
    }

    // # Private
    init {
        savedStateHandle.get<Int>(KEY1)?.also { selectedImportHostPage.set(it) }
    }

    // # State
    val selectedItemId = selectedImportHostPage.flow
    val fragFactory =
        selectedItemId.map {
            when (it) {
                R.id.importTransactionsFrag -> {
                    { ImportTransactionsFrag() }
                }
                R.id.accountsFrag -> {
                    { AccountsFrag() }
                }
                R.id.categorizeNestedGraph -> {
                    { CategorizeFrag() }
                }
                else -> error("Unknown id")
            }
        }
}