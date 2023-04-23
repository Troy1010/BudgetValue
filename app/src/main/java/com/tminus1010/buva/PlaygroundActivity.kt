package com.tminus1010.buva

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.tminus1010.buva.databinding.ActivityPlaygroundBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaygroundActivity : AppCompatActivity() {
    val vb by lazy { ActivityPlaygroundBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        vb.lineChart1.data =
            LineData(
                LineDataSet(
                    (0..10).map {
                        Entry(it.toFloat(), 10f)
                    },
                    "label1",
                ).apply {
                    color = Color.BLUE
                },
                LineDataSet(
                    (0..10).map {
                        Entry(it.toFloat(), 20f)
                    },
                    "label2",
                ),
            )
    }
}