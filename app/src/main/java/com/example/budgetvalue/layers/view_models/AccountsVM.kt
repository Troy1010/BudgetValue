package com.example.budgetvalue.layers.view_models

import androidx.lifecycle.ViewModel
import com.example.budgetvalue.layers.data_layer.Repo
import com.example.tmcommonkotlin.logz
import io.reactivex.rxjava3.subjects.PublishSubject

class AccountsVM(val repo: Repo): ViewModel() {
    val accounts = repo.getAccounts()
    val intentAddAccount = PublishSubject.create<Unit>()
    init {
        intentAddAccount.subscribe {
            logz("it:$it")
        }
    }
}