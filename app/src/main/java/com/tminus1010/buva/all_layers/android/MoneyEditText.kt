package com.tminus1010.buva.all_layers.android

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.TextView
import androidx.appcompat.R
import androidx.appcompat.widget.AppCompatEditText
import com.tminus1010.buva.all_layers.extensions.easyText
import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class MoneyEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle,
) : AppCompatEditText(context, attrs, defStyleAttr), IOnEditorActionListener {
    private val _onEditorActionListener = PublishSubject.create<Triple<TextView, Int, KeyEvent>>()
    override val onEditorActionListener: Observable<Triple<TextView, Int, KeyEvent>> = _onEditorActionListener

    init {
        easyText = "0"
        onDone { s ->
            s.toMoneyBigDecimal().toString()
                .also { if (it != s) setText(it) }
        }
        setOnEditorActionListener { v, actionId, event ->
            _onEditorActionListener.onNext(Triple(v, actionId, event))
            false
        }
        setOnFocusChangeListener { v, hasFocus ->
            val textRedef = text
            if (hasFocus) {
                if (textRedef?.getOrNull(0) == '0')
                    textRedef.delete(0, 1)
            }
        }
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                removeTextChangedListener(this)
                // If a 3rd number is added to the right of the decimal, move the decimal right.
                if (s.contains(".") && 3 <= s.split(".").last().count())
                    s.indexOfLast { it == '.' }
                        .also { s.delete(it, it + 1); s.insert(it + 1, ".") }
                addTextChangedListener(this)
            }
        })
    }
}