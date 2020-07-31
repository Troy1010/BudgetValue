package com.example.budgetvalue.layers.z_ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.example.budgetvalue.App
import com.example.budgetvalue.CODE_PICK_TRANSACTIONS_FILE
import com.example.budgetvalue.R
import com.example.budgetvalue.layers.view_models.TransactionsVM
import com.example.tmcommonkotlin.easyToast
import com.example.tmcommonkotlin.logz
import com.example.tmcommonkotlin.vmFactoryFactory
import kotlinx.android.synthetic.main.activity_host.*
import kotlinx.coroutines.runBlocking

class HostActivity : AppCompatActivity() {
    val appComponent by lazy { (applicationContext as App).appComponent }
    val transactionsVM: TransactionsVM by viewModels { vmFactoryFactory { TransactionsVM(appComponent.getRepo()) } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_import_transactions -> {
                val intent = Intent()
                intent.type = "*/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                    Intent.createChooser(intent, "Select transactions csv"),
                    CODE_PICK_TRANSACTIONS_FILE
                )
            }
            R.id.menu_print_transactions -> {
                runBlocking {
                    val transactions = appComponent.getRepo().getTransactions()
                    logz("transactions:${transactions.joinToString(",")}")
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == CODE_PICK_TRANSACTIONS_FILE && resultCode == Activity.RESULT_OK) {
            try {
                val inputStream = contentResolver.openInputStream(intent!!.data!!)!!
                transactionsVM.importTransactions(inputStream)
                easyToast("Import successful")
            } catch (e: Exception) {
                e.printStackTrace()
                easyToast("Import failed")
            }
        }
        super.onActivityResult(requestCode, resultCode, intent)
    }
}