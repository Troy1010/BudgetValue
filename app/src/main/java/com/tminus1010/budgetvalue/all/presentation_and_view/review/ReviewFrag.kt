package com.tminus1010.budgetvalue.all.presentation_and_view.review

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.all.framework.extensions.invoke
import com.tminus1010.budgetvalue.all.presentation_and_view.SelectableDuration
import com.tminus1010.budgetvalue.all.presentation_and_view._models.NoMostRecentSpend
import com.tminus1010.budgetvalue.all.presentation_and_view.bind
import com.tminus1010.budgetvalue.databinding.FragReviewBinding
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.easyToast
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class ReviewFrag : Fragment(R.layout.frag_review) {
    lateinit var vb: FragReviewBinding
    val reviewVM by viewModels<ReviewVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragReviewBinding.bind(view)
        // # Events
        reviewVM.errors.observe(viewLifecycleOwner) {
            when (it) {
                is NoMostRecentSpend -> logz("Swallowing error:${it.javaClass.simpleName}")
                else -> easyToast("An error occurred").run { logz("error:", it) }
            }
        }
        // # State
        vb.pieChart1.bind(reviewVM.pieChartVMItem)
        // ## Spinner
        val adapter = ArrayAdapter(vb.root.context, R.layout.item_text_view_without_highlight, SelectableDuration.values())
        vb.spinnerDuration.adapter = adapter
        vb.spinnerDuration.setSelection(adapter.getPosition(reviewVM.initialSelectedDuration))
        vb.spinnerDuration.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            var didFirstSelectionHappen = AtomicBoolean(false)
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (didFirstSelectionHappen.getAndSet(true))
                    reviewVM.userSelectedDuration(vb.spinnerDuration.selectedItem as SelectableDuration)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }
}