package com.example.epsandroidlibrary

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.stripe.android.model.DelicateCardDetailsApi
import com.stripe.android.view.CardInputWidget

@OptIn(DelicateCardDetailsApi::class)
public class CardFieldViewAndroid @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    init {
        val cardFieldView = CardFieldView(context, null, 0)
        cardFieldView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        addView(cardFieldView)
    }
}