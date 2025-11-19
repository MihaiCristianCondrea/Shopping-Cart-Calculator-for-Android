package com.d4rk.cartcalculator.app.cart.list.ui.components

import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.hapticSwipeToDismissBox
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.cart.list.domain.model.ui.UiHomeData
import com.d4rk.cartcalculator.core.data.database.table.ShoppingCartTable
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CartItem(
    cart : ShoppingCartTable , onDelete : (ShoppingCartTable) -> Unit , onCardClick : () -> Unit , onRename : (ShoppingCartTable) -> Unit , onShare : (ShoppingCartTable) -> Unit , uiState : UiHomeData , modifier : Modifier
) {
    val dateFormat = remember { SimpleDateFormat("dd-MM-yyyy" , Locale.getDefault()) }
    val dateString : String = remember(cart.date) { dateFormat.format(cart.date) }
    val view : View = LocalView.current
    val dismissState : SwipeToDismissBoxState = rememberSwipeToDismissBoxState(confirmValueChange = {
        if (it == SwipeToDismissBoxValue.StartToEnd || it == SwipeToDismissBoxValue.EndToStart) {
            ! uiState.showDeleteCartDialog
        }
        else {
            true
        }
    })
    var menuExpanded : Boolean by remember { mutableStateOf(value = false) }
    LaunchedEffect(key1 = dismissState.targetValue , key2 = dismissState.currentValue) {
        when {
            dismissState.currentValue == dismissState.targetValue -> dismissState.reset()
            dismissState.targetValue != SwipeToDismissBoxValue.Settled -> onDelete(cart)
        }
    }
    SwipeToDismissBox(
        modifier = modifier
                .hapticSwipeToDismissBox(swipeToDismissBoxState = dismissState)
                .padding(horizontal = SizeConstants.MediumSize) , state = dismissState , backgroundContent = {}) {
        OutlinedCard(
            shape = RoundedCornerShape(size = SizeConstants.MediumSize) , modifier = Modifier.fillMaxWidth() , onClick = {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                onCardClick()
            }) {
            Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = SizeConstants.MediumSize) , verticalAlignment = Alignment.CenterVertically , horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                            .weight(weight = 1f)
                            .padding(end = SizeConstants.SmallSize)
                ) {
                    Text(
                        text = cart.name , style = MaterialTheme.typography.titleMedium , maxLines = 1 , overflow = TextOverflow.Ellipsis
                    )
                    SmallVerticalSpacer()
                    Text(
                        text = stringResource(R.string.created_on , dateString) , style = MaterialTheme.typography.labelMedium , textAlign = TextAlign.Start
                    )
                    SmallVerticalSpacer()
                    CartCategoriesRow(cart = cart)
                }
                CartDropdownMenu(expanded = menuExpanded , onDismissRequest = { menuExpanded = false } , onDelete = {
                    menuExpanded = false
                    onDelete(cart)
                } , onShare = {
                    menuExpanded = false
                    onShare(cart)
                } , onOpen = { menuExpanded = true } , onRename = {
                    menuExpanded = false
                    onRename(cart)
                })
            }
        }
    }
}