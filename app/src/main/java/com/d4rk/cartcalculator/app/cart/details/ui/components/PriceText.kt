package com.d4rk.cartcalculator.app.cart.details.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.d4rk.android.libs.apptoolkit.core.ui.components.digits.AnimatedDigit
import java.util.Locale

@Composable
fun PriceText(price : Float , currency : String , modifier : Modifier = Modifier) {
    val formattedPrice : String = String
            .format(Locale.getDefault() , "%.2f" , price)
            .trimEnd('0')
            .trimEnd('.') + " $currency"

    Row(modifier = modifier) {
        formattedPrice.forEach { char : Char ->
            if (char.isDigit()) {
                AnimatedDigit(digit = char)
            }
            else {
                Text(text = char.toString() , style = MaterialTheme.typography.headlineSmall , fontWeight = MaterialTheme.typography.headlineSmall.fontWeight)
            }
        }
    }
}