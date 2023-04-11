package com.tminus1010.buva.ui.import_and_categorize

import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.ui.import_and_categorize.categorize.CategorizeFrag
import com.tminus1010.buva.ui.import_and_categorize.transactions.AccountsFrag
import com.tminus1010.buva.ui.import_and_categorize.transactions.ImportTransactionsFrag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ImportAndCategorizeHostVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    // # User Intent
    fun userSelectMenuItem(id: Int) {
        fragFactory.onNext(createFragFactoryFromID(id))
    }

    // # Internal
    private val subNavId = savedStateHandle.get<Int>(KEY1) ?: R.id.importTransactionsFrag
    private fun createFragFactoryFromID(id: Int): () -> Fragment {
        return when (id) {
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

    // # State
    val fragFactory = MutableStateFlow(value = createFragFactoryFromID(subNavId))
}