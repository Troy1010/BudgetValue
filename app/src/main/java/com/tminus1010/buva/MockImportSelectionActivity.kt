package com.tminus1010.buva

import android.content.res.AssetManager
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import com.tminus1010.buva.app.ImportTransactions
import com.tminus1010.buva.databinding.ActivityMockImportSelectionBinding
import com.tminus1010.buva.ui.all_features.ShowImportResultAlertDialog
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.tmcommonkotlin.androidx.ShowAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [MockImportSelectionActivity] helps choosing one of the example transaction.cvs files during testing.
 *
 * It would be nice if I could define this in the androidTest/ source set, but I could not find a way.
 */
@VisibleForTesting
@AndroidEntryPoint
class MockImportSelectionActivity : AppCompatActivity() {
    private val vb by lazy { ActivityMockImportSelectionBinding.inflate(layoutInflater) }

    @Inject
    lateinit var androidTestAssetsProvider: AndroidTestAssetsProvider

    @Inject
    lateinit var importTransactions: ImportTransactions
    private val showImportResultAlertDialog by lazy { ShowImportResultAlertDialog(ShowAlertDialog(this)) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        vb.buttonsview.buttons =
            (androidTestAssetsProvider.get().list("transactions")
                ?.map { "transactions/$it" }
                ?: emptyList())
                .withIndex().map { (i, s) ->
                    ButtonVMItem(
                        title = "Import Transaction $i",
                        onClick = {
                            GlobalScope.launch {
                                showImportResultAlertDialog(importTransactions(androidTestAssetsProvider.get().open(s).buffered()))
                                finish()
                            }
                        },
                    )
                }
    }

    open class AndroidTestAssetsProvider @Inject constructor() {
        open fun get(): AssetManager = TODO()
    }
}