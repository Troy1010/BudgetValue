package com.tminus1010.budgetvalue

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tminus1010.budgetvalue.extensions.viewModels2
import com.tminus1010.budgetvalue.layer_ui.TransactionsVM
import kotlinx.android.synthetic.devEnv1.activity_mock_import_selection.*

class MockImportSelectionActivity: AppCompatActivity(R.layout.activity_mock_import_selection) {
    val app by lazy { application as App }
    val transactionsVM: TransactionsVM by viewModels2 { TransactionsVM(app.appComponent.getRepo(), app.appComponent.getDatePeriodGetter()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // # Clicks
        btn_import_transactions_1.setOnClickListener {
            assets.open("transactions_2013487188.csv").buffered()
                .also { transactionsVM.importTransactions(it) }
            finish()
        }
    }
}