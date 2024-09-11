package com.dev.fabricio.gonzalez.mediaapp

import App
import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

            App(
                isDarkTheme = isSystemInDarkTheme(), appColor = when {
                    (isSystemInDarkTheme() && dynamicColor) -> {
                        dynamicDarkColorScheme(LocalContext.current)
                    }

                    dynamicColor && !isSystemInDarkTheme() -> dynamicLightColorScheme(
                        LocalContext.current
                    )

                    else -> null
                }
            )
        }
    }
}

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger(if (isDebug()) Level.ERROR else Level.NONE)
            androidContext(this@AndroidApp)
        }
    }
}

fun Context.isDebug() = 0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE

@Preview
@Composable
fun AppAndroidPreview() {
    App(
        isDarkTheme = isSystemInDarkTheme(), appColor = null
    )
}