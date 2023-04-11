package com.tminus1010.buva.ui.importZ

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.ui.importZ.categorize.CategorizeFrag
import com.tminus1010.buva.ui.importZ.transactions.AccountsFrag
import com.tminus1010.buva.ui.importZ.transactions.ImportTransactionsFrag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ImportHostVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    // # User Intent
    fun userSelectMenuItem(id: Int) {
        selectedItemId.onNext(id)
    }

    // # Private
    private val subNavId = savedStateHandle.get<Int>(KEY1) ?: R.id.importTransactionsFrag

    // # State
    val selectedItemId = MutableStateFlow(value = subNavId)
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