package com.luanbarbosagomes.poiapp.utils

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.luanbarbosagomes.poiapp.R

object BrowserUtils {

    fun openOnTab(context: Context, url: String) {
        CustomTabsIntent
            .Builder()
            .setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .build()
            .launchUrl(context, Uri.parse(url))
    }
}