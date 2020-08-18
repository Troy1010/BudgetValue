package com.example.budgetvalue.layers.z_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.budgetvalue.R
import com.example.tmcommonkotlin.GenericRecyclerViewAdapter
import kotlinx.android.synthetic.main.frag_accounts.*

class AccountsFrag: Fragment(), GenericRecyclerViewAdapter.Callbacks {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_accounts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerview_accounts.adapter = GenericRecyclerViewAdapter(this, requireContext(), R.layout.item_account)
    }

    override fun bindRecyclerItemView(view: View, i: Int) {
        TODO("Not yet implemented")
    }

    override fun getRecyclerDataSize(): Int {
        TODO("Not yet implemented")
    }
}