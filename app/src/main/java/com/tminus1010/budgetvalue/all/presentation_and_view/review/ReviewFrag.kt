package com.tminus1010.budgetvalue.all.presentation_and_view.review

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.models.CategoryAmounts
import com.tminus1010.budgetvalue.databinding.FragReviewBinding
import com.tminus1010.tmcommonkotlin.core.logx
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.absoluteValue

@AndroidEntryPoint
class ReviewFrag : Fragment(R.layout.frag_review) {
    lateinit var vb: FragReviewBinding
    val reviewVM by viewModels<ReviewVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragReviewBinding.bind(view)
        vb.pieChart1.bind(reviewVM.chartData) { data = createPieData(it); invalidate() }
    }

    private fun createPieData(categoryAmounts: CategoryAmounts): PieData {
        val entries = categoryAmounts.map { PieEntry(it.value.toFloat().absoluteValue.logx("PieEntryValue")) }
        val dataSet = PieDataSet(entries, "Election Results")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0F, 40F)
        dataSet.selectionShift = 5f

        // add a lot of colors
        val colors = ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
        colors.add(ColorTemplate.getHoloBlue())
        dataSet.colors = colors
//        dataSet.selectionShift = 0f
        return PieData(dataSet).apply {
            setValueFormatter(PercentFormatter())
            setValueTextSize(11f)
            setValueTextColor(Color.WHITE)
        }
    }
}