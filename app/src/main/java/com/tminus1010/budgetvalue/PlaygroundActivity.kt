package com.tminus1010.budgetvalue

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tminus1010.budgetvalue.databinding.ActivityPlaygroundBinding
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.ButtonVMItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaygroundActivity : AppCompatActivity() {
    val vb by lazy { ActivityPlaygroundBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        vb.buttonsview.buttons =
            listOf(
                ButtonVMItem(
                    title = "Do Nothing",
                    onClick = {}
                )
            )
    }
}