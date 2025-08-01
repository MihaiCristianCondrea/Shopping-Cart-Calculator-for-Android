package com.d4rk.cartcalculator.app.main.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.d4rk.android.libs.apptoolkit.app.main.utils.InAppUpdateHelper
import com.d4rk.android.libs.apptoolkit.app.startup.ui.StartupActivity
import com.d4rk.android.libs.apptoolkit.app.theme.style.AppTheme
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.ConsentFormHelper
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.ConsentManagerHelper
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.IntentsHelper
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.ReviewHelper
import com.d4rk.cartcalculator.core.data.datastore.DataStore
import com.d4rk.cartcalculator.core.utils.helpers.LanguageCurrencyHelper
import com.google.android.gms.ads.MobileAds
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.ump.ConsentInformation
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity() {

    private val dataStore : DataStore by inject()
    private var updateResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) {}
    private lateinit var viewModel : MainViewModel
    private var keepSplashVisible : Boolean = true

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashVisible }
        enableEdgeToEdge()
        initializeDependencies()
        handleStartup()
        checkInAppReview()
    }

    override fun onResume() {
        super.onResume()
        checkForUpdates()
        checkUserConsent()
    }

    private fun initializeDependencies() {
        CoroutineScope(context = Dispatchers.IO).launch {
            MobileAds.initialize(this@MainActivity) {}
            ConsentManagerHelper.applyInitialConsent(dataStore = dataStore)
        }

        viewModel = getViewModel { parametersOf(updateResultLauncher) }
    }

    private fun handleStartup() {
        lifecycleScope.launch {
            val isFirstLaunch : Boolean = dataStore.startup.first()

            val currentCurrency : String = dataStore.getCurrency().first()
            if (currentCurrency.isBlank()) {
                val defaultCurrency : String? = LanguageCurrencyHelper.getDefaultCurrencyForLocale(applicationContext = this@MainActivity)
                defaultCurrency?.let { selectedCurrency : String ->
                    dataStore.saveCurrency(currency = selectedCurrency)
                }
            }

            keepSplashVisible = false
            if (isFirstLaunch) {
                startStartupActivity()
            }
            else {
                setMainActivityContent()
            }
        }
    }

    private fun startStartupActivity() {
        IntentsHelper.openActivity(context = this , activityClass = StartupActivity::class.java)
        finish()
    }

    private fun setMainActivityContent() {
        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize() , color = MaterialTheme.colorScheme.background) {
                    MainScreen()
                }
            }
        }
    }

    private fun checkUserConsent() {
        val consentInfo: ConsentInformation = UserMessagingPlatform.getConsentInformation(this)
        ConsentFormHelper.showConsentFormIfRequired(activity = this , consentInfo = consentInfo)
    }

    private fun checkInAppReview() {
        lifecycleScope.launch {
            val sessionCount: Int = dataStore.sessionCount.first()
            val hasPrompted: Boolean = dataStore.hasPromptedReview.first()
            ReviewHelper.launchInAppReviewIfEligible(
                activity = this@MainActivity,
                sessionCount = sessionCount,
                hasPromptedBefore = hasPrompted
            ) {
                lifecycleScope.launch { dataStore.setHasPromptedReview(true) }
            }
            dataStore.incrementSessionCount()
        }
    }

    private fun checkForUpdates() {
        lifecycleScope.launch {
            InAppUpdateHelper.performUpdate(
                appUpdateManager = AppUpdateManagerFactory.create(this@MainActivity),
                updateResultLauncher = updateResultLauncher,
            )
        }
    }
}