package com.tminus1010.budgetvalue

import android.content.res.AssetManager
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import com.tminus1010.budgetvalue.app.ImportTransactions
import com.tminus1010.budgetvalue.databinding.ActivityMockImportSelectionBinding
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.tmcommonkotlin.view.extensions.easyToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * [MockImportSelectionActivity] helps choosing one of the example transaction.cvs files during testing.
 *
 * It would be nice if I could define this in the androidTest/ source set, but I could not find a way.
 */
@VisibleForTesting
@AndroidEntryPoint
class MockImportSelectionActivity : AppCompatActivity() {
    val vb by lazy { ActivityMockImportSelectionBinding.inflate(layoutInflater) }

    @Inject
    lateinit var androidTestAssetsProvider: AndroidTestAssetsProvider

    @Inject
    lateinit var importTransactions: ImportTransactions

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
                            runBlocking { importTransactions(androidTestAssetsProvider.get().open(s).buffered()) }
                            application.easyToast(getString(R.string.import_successful))
                            finish()
                        }
                    )
                }
    }

    open class AndroidTestAssetsProvider @Inject constructor() {
        open fun get(): AssetManager = TODO()
    }
}