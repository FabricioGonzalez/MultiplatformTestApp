import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun BrowserPage(
    url: String,
    onBrowserClosed: () -> Unit
) {
    val customTabsServiceConnection: CustomTabsServiceConnection?
    var mClient: CustomTabsClient?
    var customTabsSession: CustomTabsSession? = null
    val context = LocalContext.current


    val actionResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),

        onResult = {
            onBrowserClosed()
        }
    )

    customTabsServiceConnection = object : CustomTabsServiceConnection() {
        override fun onCustomTabsServiceConnected(
            componentName: ComponentName,
            customTabsClient: CustomTabsClient
        ) { //pre-warning means to fast the surfing
            mClient = customTabsClient
            mClient?.warmup(0L)
            customTabsSession = mClient?.newSession(null)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mClient = null
        }
    }

    val item = CustomTabsClient.getPackageName(
        context,
        listOf("com.brave.browser", "com.android.chrome"),
        true
    )

    CustomTabsClient.bindCustomTabsService(
        context,
        item,
        customTabsServiceConnection as CustomTabsServiceConnection
    )
    val uri = Uri.parse(url)

    val colorPrimaryLight =
        MaterialTheme.colorScheme.primaryContainer

    val intentBuilder = CustomTabsIntent.Builder(customTabsSession)
        .setUrlBarHidingEnabled(true)
        .setInitialActivityHeightPx(400)
        // set the default color scheme
        .setDefaultColorSchemeParams(
            CustomTabColorSchemeParams.Builder()
                .setToolbarColor(colorPrimaryLight.toArgb())
                .build()
        )
    // set the exit animations
    intentBuilder.setExitAnimations(
        context,
        android.R.anim.slide_in_left,
        android.R.anim.slide_out_right
    )

    //build custom tabs intent
    val customTabsIntent = intentBuilder.build()

    customTabsIntent.apply {
        intent.putExtra("com.google.android.apps.chrome.EXTRA_OPEN_NEW_INCOGNITO_TAB", true)
        intent.setPackage(item)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
    }
    customTabsIntent.intent.setData(uri)
    LaunchedEffect(key1 = "", block = {
        actionResult.launch(customTabsIntent.intent)
    })

}

@Composable
actual fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(orientation) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}