package com.d4rk.cartcalculator.app.main.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import com.d4rk.android.libs.apptoolkit.R
import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.domain.model.navigation.NavigationDrawerItem
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.successData
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.cartcalculator.app.main.domain.actions.MainAction
import com.d4rk.cartcalculator.app.main.domain.actions.MainEvent
import com.d4rk.cartcalculator.app.main.domain.model.UiMainScreen

class MainViewModel(private val dispatcherProvider : DispatcherProvider) : ScreenViewModel<UiMainScreen , MainEvent , MainAction>(initialState = UiStateScreen(data = UiMainScreen())) {

    init {
        onEvent(event = MainEvent.LoadNavigation)
    }

    override fun onEvent(event : MainEvent) {
        when (event) {
            is MainEvent.LoadNavigation -> loadNavigationItems()
        }
    }

    private fun loadNavigationItems() {
        launch(context = dispatcherProvider.default) {
            screenState.successData {
                copy(
                    navigationDrawerItems = listOf(
                        NavigationDrawerItem(
                            title = R.string.settings , selectedIcon = Icons.Outlined.Settings
                        ) , NavigationDrawerItem(
                            title = R.string.help_and_feedback , selectedIcon = Icons.AutoMirrored.Outlined.HelpOutline
                        ) , NavigationDrawerItem(
                            title = R.string.updates , selectedIcon = Icons.AutoMirrored.Outlined.EventNote
                        ) , NavigationDrawerItem(
                            title = R.string.share , selectedIcon = Icons.Outlined.Share
                        )
                    )
                )
            }
        }
    }
}