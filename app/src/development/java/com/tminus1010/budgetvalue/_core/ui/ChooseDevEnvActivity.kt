package com.tminus1010.budgetvalue._core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.middleware.ui.ButtonPartial
import com.tminus1010.budgetvalue._core.middleware.ui.GenViewHolder2
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue._core.ui.view_binding.bind
import com.tminus1010.budgetvalue.databinding.ActivityChooseDevEnvBinding
import com.tminus1010.budgetvalue.databinding.ItemButtonBinding
import com.tminus1010.tmcommonkotlin.misc.logz

class ChooseDevEnvActivity: AppCompatActivity(R.layout.activity_choose_dev_env) {
    val vb by viewBinding(ActivityChooseDevEnvBinding::inflate)
    val rvData = listOf(
        ButtonPartial("DevEnv1") { logz("hi1") },
        ButtonPartial("DevEnv2") { logz("hi2") },
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        vb.recyclerview.layoutManager = LinearLayoutManager(this)
        vb.recyclerview.adapter = object : RecyclerView.Adapter<GenViewHolder2<ItemButtonBinding>>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenViewHolder2<ItemButtonBinding> =
                ItemButtonBinding.inflate(LayoutInflater.from(this@ChooseDevEnvActivity), parent, false)
                    .let { GenViewHolder2(it) }

            override fun onBindViewHolder(holder: GenViewHolder2<ItemButtonBinding>, pos: Int) =
                holder.vb.bind(rvData[pos])

            override fun getItemCount(): Int = rvData.size
        }
    }
}