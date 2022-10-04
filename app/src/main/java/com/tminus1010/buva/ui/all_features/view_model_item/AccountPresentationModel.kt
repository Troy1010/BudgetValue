package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import android.widget.EditText
import com.tminus1010.buva.all_layers.extensions.easyText3
import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.buva.data.AccountsRepo
import com.tminus1010.buva.databinding.ItemAccountBinding
import com.tminus1010.buva.databinding.ItemMoneyEditTextBinding
import com.tminus1010.buva.domain.Account
import com.tminus1010.buva.all_layers.android.onDone
import com.tminus1010.tmcommonkotlin.customviews.IHasToViewItemRecipe
import com.tminus1010.tmcommonkotlin.customviews.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipe3
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AccountPresentationModel(
    private val account: Account,
    private val accountsRepo: AccountsRepo,
): IHasToViewItemRecipe {
    // # User Intents
    fun userSetTitle(s: String) {
        GlobalScope.launch { accountsRepo.update(account.copy(name = s)) }
    }

    fun userSetAmount(s: String) {
        GlobalScope.launch { accountsRepo.update(account.copy(amount = s.toMoneyBigDecimal())) }
    }

    fun userDeleteAccount() {
        GlobalScope.launch { accountsRepo.delete(account) }
    }

    // # State
    fun bind(vb: ItemAccountBinding) {
        vb.edittextName.easyText3 = account.name
        vb.edittextName.onDone(::userSetTitle)
        MenuVMItems(MenuVMItem("Delete", ::userDeleteAccount)).bind(vb.edittextName)
        vb.edittextAmount.easyText3 = account.amount.toString()
        vb.edittextAmount.onDone(::userSetAmount)
        MenuVMItems(MenuVMItem("Delete", ::userDeleteAccount)).bind(vb.edittextAmount)
    }

    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3(context, ItemAccountBinding::inflate) { vb ->
            bind(vb)
        }
    }
}