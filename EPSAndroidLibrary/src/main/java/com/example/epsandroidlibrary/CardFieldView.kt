package com.example.epsandroidlibrary

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.Toast
import com.stripe.android.model.DelicateCardDetailsApi
import com.stripe.android.view.CardInputWidget

@OptIn(DelicateCardDetailsApi::class)
class CardFieldView(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val cardInputWidget: CardInputWidget

    init {
        cardInputWidget = CardInputWidget(context)
        orientation = VERTICAL
        cardInputWidget.postalCodeEnabled = false
        addView(cardInputWidget)
        cardInputWidget.setCardValidCallback { isValid, card ->
            if (isValid) {
                SDKResolver.updateCardInfo(
                    TCardInfo(
                        cardInputWidget.cardParams?.number,
                        cardInputWidget.cardParams?.cvc,
                        cardInputWidget.cardParams?.expMonth,
                        cardInputWidget.cardParams?.expYear

                    )
                )
                // Card input is valid
                // You can access card details using the `card` parameter
            } else {
                Toast.makeText(context, "Invalid Card Info", Toast.LENGTH_LONG).show()
            }
        }


        // Card input is valid
        // You can access card details using the `card` parameter

    }
}