package com.tminus1010.buva.ui.import_and_categorize

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.buva.ui.categorize.CategorizeFrag
import com.tminus1010.buva.ui.importZ.ImportFrag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class ImportAndCategorizeHostVM @Inject constructor(
) : ViewModel() {
    val frag = MutableStateFlow<Class<out Fragment>>(ImportFrag::class.java)
    val buttons =
        flowOf(
            listOf(
                ButtonVMItem(
                    title = "Import",
                    onClick = { frag.onNext(ImportFrag::class.java) }
                ),
                ButtonVMItem(
                    title = "Categorize",
                    onClick = { frag.onNext(CategorizeFrag::class.java) }
                ),
            )
        )
}