package com.tminus1010.buva.environment.android_wrapper

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.Transaction
import com.tminus1010.buva.ui.category_details.CategoryDetailsFrag
import com.tminus1010.buva.ui.choose_transaction.ChooseTransactionFrag
import com.tminus1010.buva.ui.set_string.SetStringFrag
import com.tminus1010.tmcommonkotlin.androidx.launchOnMainThread
import dagger.Reusable
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Reusable
class AndroidNavigationWrapperImpl @Inject constructor() : AndroidNavigationWrapper {
    override fun navToCreateCategory() = launchOnMainThread {
        CategoryDetailsFrag.navTo(nav, null)
    }

    override fun navToEditCategory(category: Category) = launchOnMainThread {
        CategoryDetailsFrag.navTo(nav, category)
    }

    override fun navToFutures() = launchOnMainThread {
        nav.navigate(R.id.futuresFrag)
    }

    override fun navToTransactions() = launchOnMainThread {
        nav.navigate(R.id.transactionsFrag)
    }

    override fun navToTransactions(transactions: List<Transaction>) = launchOnMainThread {
        nav.navigate(
            R.id.transactionsFrag,
            Bundle().apply {
                putParcelable(KEY1, ParcelableTransactionToBooleanLambdaWrapper { it.id in transactions.map { it.id } })
            },
        )
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

    override fun navTo(id: Int) = launchOnMainThread {
        nav.navigate(id)
    }

    private val nav get() = Companion.nav ?: error("This class expects Companion.nav to be assigned")

    companion object {
        // This pattern can cause memory leaks. However, this nav controller lasts for the entire application, so it should be fine.
        @SuppressLint("StaticFieldLeak")
        var nav: NavController? = null
    }
}