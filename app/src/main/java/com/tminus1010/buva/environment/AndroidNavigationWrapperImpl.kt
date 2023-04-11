package com.tminus1010.buva.environment

import android.annotation.SuppressLint
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.Transaction
import com.tminus1010.buva.ui.category_details.CategoryDetailsFrag
import com.tminus1010.buva.ui.choose_transaction.ChooseTransactionFrag
import com.tminus1010.buva.ui.importZ.ImportHostFrag
import com.tminus1010.buva.ui.set_string.SetStringFrag
import com.tminus1010.tmcommonkotlin.androidx.launchOnMainThread
import dagger.Reusable
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Reusable
class AndroidNavigationWrapperImpl @Inject constructor() : AndroidNavigationWrapper {
    override fun navToImportTransactions() = launchOnMainThread {
        ImportHostFrag.navTo(nav, R.id.importTransactionsFrag)
    }

    override fun navToAccounts() = launchOnMainThread {
        ImportHostFrag.navTo(nav, R.id.accountsFrag)
    }

    override fun navToCreateCategory() = launchOnMainThread {
        CategoryDetailsFrag.navTo(nav, null)
    }

    override fun navToEditCategory(category: Category) = launchOnMainThread {
        CategoryDetailsFrag.navTo(nav, category)
    }

    override fun navToCategorize() = launchOnMainThread {
        ImportHostFrag.navTo(nav, R.id.categorizeNestedGraph)
    }

    override fun navToFutures() = launchOnMainThread {
        nav.navigate(R.id.futuresFrag)
    }

    override fun navToTransactions() = launchOnMainThread {
        nav.navigate(R.id.transactionsFrag)
    }

    override fun navToHistory() = launchOnMainThread {
        nav.navigate(R.id.historyFrag)
    }

    override fun navToCreateFuture() = launchOnMainThread {
        nav.navigate(R.id.createFutureFrag)
    }

    override fun navUp() = launchOnMainThread {
        nav.navigateUp()
    }

    override suspend fun navToSetString(s: String): String? = suspendCoroutine { downstream ->
        launchOnMainThread {
            SetStringFrag.navTo(nav, s, callback = { downstream.resume(it) })
        }
    }

    override suspend fun navToChooseTransaction(): Transaction? = suspendCoroutine { downstream ->
        launchOnMainThread {
            ChooseTransactionFrag.navTo(nav, callback = { downstream.resume(it) })
        }
    }

    private val nav get() = Companion.nav ?: error("This class expects Companion.nav to be assigned")

    companion object {
        // This pattern can cause memory leaks. However, this project only has 1 Activity, so a memory leak is unlikely
        @SuppressLint("StaticFieldLeak")
        var nav: NavController? = null
    }
}