package com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3

import android.view.LayoutInflater
import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tminus1010.budgetvalue.__core_testing.app
import com.tminus1010.budgetvalue.databinding.ItemTextViewBinding
import com.tminus1010.tmcommonkotlin.misc.generateUniqueID
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ViewItemRecipe3__Test {
//    @Test
//    fun intrinsicHeight2() {
//        // # Given
//        val viewItemRecipe = ViewItemRecipe3__(
//            app,
//            ItemTextViewBinding::inflate,
//            null,
//            { vb ->
//                vb.textview.text = generateUniqueID()
//            }
//        )
//        // # When & Then
//        logz("intrinsicWidth:${viewItemRecipe.intrinsicWidth}")
//        logz("viewItemRecipe.intrinsicHeight:${viewItemRecipe.intrinsicHeight}")
//        logz("viewItemRecipe.intrinsicHeight2(877):${viewItemRecipe.intrinsicHeight2(877)}")
//        assertNotEquals(viewItemRecipe.intrinsicHeight, viewItemRecipe.intrinsicHeight2(877))
//    }

    @Test
    fun intrinsicHeightPlayground() {
        val v = ItemTextViewBinding.inflate(LayoutInflater.from(app)).textview
        v.text = generateUniqueID()
        v.measure(
            View.MeasureSpec.makeMeasureSpec(500, View.MeasureSpec.AT_MOST),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        )
        v.requestLayout()
        Thread.sleep(100)
        logz("height1:${v.measuredHeight}")
        logz("width1:${v.measuredWidth}")
        v.measure(
            View.MeasureSpec.makeMeasureSpec(2000, View.MeasureSpec.AT_MOST),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        )
        v.requestLayout()
        Thread.sleep(100)
        logz("height2:${v.measuredHeight}")
        logz("width2:${v.measuredWidth}")
    }
}