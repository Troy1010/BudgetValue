package com.tminus1010.buva.ui.review.line_chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.buva.ui.all_features.ThrobberSharedVM
import com.tminus1010.buva.ui.all_features.view_model_item.LineChartVMItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@HiltViewModel
class ReviewLineChartVM @Inject constructor(
    private val reviewLineChartService: ReviewLineChartService,
    private val throbberSharedVM: ThrobberSharedVM,
) : ViewModel() {
    private val mapLabelToValues =
        reviewLineChartService.mapLabelToValues
            .let(throbberSharedVM::decorate)
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val lineChartVMItem =
        LineChartVMItem(
            mapLabelToValues = mapLabelToValues,
        )
}