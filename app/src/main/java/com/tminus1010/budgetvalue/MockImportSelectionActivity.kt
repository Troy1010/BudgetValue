package com.tminus1010.budgetvalue

import android.content.res.AssetManager
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import com.tminus1010.budgetvalue.data.service.ImportTransactions
import com.tminus1010.budgetvalue.databinding.ActivityMockImportSelectionBinding
import com.tminus1010.budgetvalue.ui.all_features.model.ButtonVMItem
import com.tminus1010.tmcommonkotlin.view.extensions.easyToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@VisibleForTesting
@AndroidEntryPoint
class MockImportSelectionActivity : AppCompatActivity() {
    val vb by lazy { ActivityMockImportSelectionBinding.inflate(layoutInflater) }

    @Inject
    lateinit var assets2: AssetManager

    @Inject
    lateinit var importTransactions: ImportTransactions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        vb.buttonsview.buttons =
            (assets2.list("transactions")
                ?.map { "transactions/$it" }
                ?: emptyList())
                .withIndex().map { (i, s) ->
                    ButtonVMItem(
                        title = "Import Transaction $i",
                        onClick = {
                            importTransactions(assets2.open(s).buffered()).subscribe()
                            application.easyToast(getString(R.string.import_successful))
                            finish()
                        }
                    )
                }
    }
}