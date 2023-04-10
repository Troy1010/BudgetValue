package com.tminus1010.buva.ui.import_and_categorize

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.ui.import_and_categorize.categorize.CategorizeFrag
import com.tminus1010.buva.ui.import_and_categorize.importZ.ImportFrag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ImportAndCategorizeHostVM @Inject constructor(
) : ViewModel() {
    // # User Intent
    fun userSelectMenuItem(id: Int) {
        when (id) {
            R.id.importFrag ->
                _frag.onNext(ImportFrag::class.java)
            R.id.categorizeFrag ->
                _frag.onNext(CategorizeFrag::class.java)
            else -> error("Unknown id")
        }
    }

    // # State
    private val _frag = MutableStateFlow<Class<out Fragment>>(ImportFrag::class.java)
    val frag = _frag.map { { it.newInstance() } }
}