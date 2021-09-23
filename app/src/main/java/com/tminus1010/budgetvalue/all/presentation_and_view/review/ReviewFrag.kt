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
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ReviewFrag : Fragment(R.layout.frag_review) {
    lateinit var vb: FragReviewBinding
    val reviewVM by viewModels<ReviewVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragReviewBinding.bind(view)
        vb.pieChart1.bind(reviewVM.chartData) {
            logz("chartData:$it")
            setData(it)
//            vb.pieChart1.data = setData(it)
//            vb.pieChart1.highlightValues(null)
//            vb.pieChart1.invalidate()
        }
    }

    private fun setData(categoryAmounts: CategoryAmounts) {
        val entries = categoryAmounts.map { PieEntry((0..100).random().toFloat().logx("PieEntryValue")) }

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
//        val entries = ArrayList<PieEntry>()
//        for (i in 0 until 10) {
//            entries.add(
//                PieEntry((0..100).random().toFloat())
////                PieEntry(
////                    (Math.random() * range + range / 5).toFloat(),
////                    parties.get(i % parties.length),
////                    resources.getDrawable(R.drawable.star)
////                )
//            )
//        }
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
        //dataSet.setSelectionShift(0f);
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        vb.pieChart1.data = data

        // undo all highlights
        vb.pieChart1.highlightValues(null)
        vb.pieChart1.invalidate()
    }


//    private fun createPieData(categoryAmounts: CategoryAmounts): PieData {
//        val entries = categoryAmounts.map { PieEntry(it.value.toFloat(), it.key.name) }
//        val dataSet = PieDataSet(entries, "Spends")
//        dataSet.setDrawIcons(false)
//        dataSet.sliceSpace = 3f
//        dataSet.iconsOffset = MPPointF(0F, 40F)
//        dataSet.selectionShift = 5f
//
//        // add a lot of colors
//        val colors = ArrayList<Int>()
//        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
//        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
//        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
//        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
//        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
//        colors.add(ColorTemplate.getHoloBlue())
//        dataSet.colors = colors
//        //dataSet.setSelectionShift(0f);
//        val data = PieData(dataSet)
//        data.setValueFormatter(PercentFormatter())
//        data.setValueTextSize(11f)
//        data.setValueTextColor(Color.WHITE)
//        return data
//    }
}