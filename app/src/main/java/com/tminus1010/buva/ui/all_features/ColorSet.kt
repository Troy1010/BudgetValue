package com.tminus1010.buva.ui.all_features

import com.github.mikephil.charting.utils.ColorTemplate
import java.util.concurrent.atomic.AtomicInteger


object ColorSet {
    val one =
        sequenceOf<Int>()
            .plus(ColorTemplate.VORDIPLOM_COLORS.toList())
            .plus(ColorTemplate.JOYFUL_COLORS.toList())
            .plus(ColorTemplate.COLORFUL_COLORS.toList())
            .plus(ColorTemplate.PASTEL_COLORS.toList())
            .plus(ColorTemplate.MATERIAL_COLORS.toList())
            .plus(ColorTemplate.LIBERTY_COLORS.toList())
            .toList()

    fun next() = one[counter.incrementAndGet() % one.size]
    private val counter = AtomicInteger()
}