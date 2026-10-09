package com.example.util

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.WarmHoney
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

object AdMobHelper {
    const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    private var interstitialAd: InterstitialAd? = null
    private var isAdLoading = false

    fun isRunningOnEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk" == Build.PRODUCT
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator"))
    }

    fun isGooglePlayServicesAvailable(context: Context): Boolean {
        if (isRunningOnEmulator()) return false
        return try {
            val availability = GoogleApiAvailability.getInstance()
            availability.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
        } catch (_: Throwable) {
            false
        }
    }

    fun initialize(context: Context) {
        if (isRunningOnEmulator() || !isGooglePlayServicesAvailable(context)) {
            // Bypass SDK initialization on emulators to prevent Mesa/Chromium/AdServices errors
            return
        }

        try {
            val requestConfig = RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
                .build()
            MobileAds.setRequestConfiguration(requestConfig)

            MobileAds.initialize(context) {}
        } catch (_: Throwable) {}
    }

    fun loadInterstitialAd(context: Context) {
        if (isRunningOnEmulator() || !isGooglePlayServicesAvailable(context)) return
        if (interstitialAd != null || isAdLoading) return
        isAdLoading = true
        try {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                INTERSTITIAL_AD_UNIT_ID,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        isAdLoading = false
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        interstitialAd = null
                        isAdLoading = false
                    }
                }
            )
        } catch (_: Throwable) {
            isAdLoading = false
        }
    }

    fun showInterstitialAd(activity: Activity, onAdDismissed: () -> Unit) {
        if (isRunningOnEmulator() || !isGooglePlayServicesAvailable(activity)) {
            onAdDismissed()
            return
        }

        val ad = interstitialAd
        if (ad != null) {
            try {
                ad.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        interstitialAd = null
                        loadInterstitialAd(activity)
                        onAdDismissed()
                    }

                    override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                        interstitialAd = null
                        loadInterstitialAd(activity)
                        onAdDismissed()
                    }
                }
                ad.show(activity)
            } catch (_: Throwable) {
                interstitialAd = null
                onAdDismissed()
            }
        } else {
            loadInterstitialAd(activity)
            onAdDismissed()
        }
    }
}

@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = AdMobHelper.BANNER_AD_UNIT_ID
) {
    val context = LocalContext.current
    val shouldLoadLiveAd = remember {
        !AdMobHelper.isRunningOnEmulator() && AdMobHelper.isGooglePlayServicesAvailable(context)
    }

    if (!shouldLoadLiveAd) {
        // When running in an emulator or environment without Play Services, render native Sponsored banner cleanly
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(54.dp),
            contentAlignment = Alignment.Center
        ) {
            SponsoredFallbackBanner()
        }
        return
    }

    var isAdLoaded by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    val adView = remember {
        try {
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId

                try {
                    setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                } catch (_: Throwable) {}

                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        isAdLoaded = true
                        hasError = false
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        isAdLoaded = false
                        hasError = true
                    }
                }

                try {
                    loadAd(AdRequest.Builder().build())
                } catch (_: Throwable) {
                    hasError = true
                }
            }
        } catch (_: Throwable) {
            null
        }
    }

    DisposableEffect(adView) {
        onDispose {
            try {
                adView?.destroy()
            } catch (_: Throwable) {}
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        contentAlignment = Alignment.Center
    ) {
        if (adView != null && !hasError) {
            AndroidView(
                factory = { adView },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
        } else {
            SponsoredFallbackBanner()
        }
    }
}

@Composable
private fun SponsoredFallbackBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFFFF9ED))
            .border(1.dp, WarmHoney.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("sponsored_fallback_banner"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(WarmHoney.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = WarmHoney,
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = "National Scholarship & Education Grant 2026",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B4505)
                )
                Text(
                    text = "100% Fee Concession Assistance for Students",
                    fontSize = 9.5.sp,
                    color = Color(0xFF8B6B38)
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFE5DAC8))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "Ad",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A3E38)
            )
        }
    }
}
