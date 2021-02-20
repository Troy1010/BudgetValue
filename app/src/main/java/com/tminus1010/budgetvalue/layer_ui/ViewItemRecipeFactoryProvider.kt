package com.tminus1010.budgetvalue.layer_ui

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.layer_ui.TMTableView.ViewItemRecipeFactory
import com.tminus1010.budgetvalue.layer_ui.misc.bindIncoming
import com.tminus1010.budgetvalue.layer_ui.misc.bindOutgoing
import com.tminus1010.budgetvalue.model_app.Category
import com.tminus1010.budgetvalue.toBigDecimalSafe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal

class ViewItemRecipeFactoryProvider(val context: Context) {
    val cellRecipeFactory = ViewItemRecipeFactory.createCellRecipeFactory(context)
    val headerRecipeFactory = ViewItemRecipeFactory.createHeaderRecipeFactory(context)
    fun twoWayBigDecimalRecipeFactory(publisher: Subject<BigDecimal>) = ViewItemRecipeFactory<EditText, Observable<BigDecimal>>(
        { View.inflate(context, R.layout.tableview_text_edit, null) as EditText },
        { view, bs ->
            view.bindIncoming(bs)
            view.bindOutgoing(publisher, { it.toBigDecimalSafe() }) { it }
        }
    )
    fun twoWayCARecipeFactory(publisher: Subject<Pair<Category, BigDecimal>>) = ViewItemRecipeFactory<EditText, Pair<Category, Observable<BigDecimal>>>(
        { View.inflate(context, R.layout.tableview_text_edit, null) as EditText },
        { view, (category, bs) ->
            view.bindIncoming(bs)
            view.bindOutgoing(publisher, { Pair(category, it.toBigDecimalSafe()) }) { it.second }
        }
    )
    fun outgoingCARecipeFactory(publisher: Subject<Pair<Category, BigDecimal>>) = ViewItemRecipeFactory<EditText, Pair<Category, BigDecimal>>(
        { View.inflate(context, R.layout.tableview_text_edit, null) as EditText },
        { view, (category, amount) ->
            view.setText(amount.toString())
            view.bindOutgoing(publisher, { Pair(category, it.toBigDecimalSafe()) }) { it.second }
        }
    )
    val incomingBigDecimalRecipeFactory = ViewItemRecipeFactory<TextView, Observable<BigDecimal>>(
        { View.inflate(context, R.layout.tableview_text_view, null) as TextView },
        { v, bs -> v.bindIncoming(bs) }
    )
    val titledDividerRecipeFactory = ViewItemRecipeFactory<TextView, String>(
        { View.inflate(context, R.layout.tableview_titled_divider, null) as TextView },
        { v, s -> v.text = s }
    )
    fun outgoingBigDecimalRecipeFactory(publisher: Subject<BigDecimal>) = ViewItemRecipeFactory<EditText, BigDecimal>(
        { View.inflate(context, R.layout.tableview_text_edit, null) as EditText },
        { view, amount ->
            view.setText(amount.toString())
            view.bindOutgoing(publisher, { it.toBigDecimalSafe() }) { it }
        }
    )
}