package multiplatform

import androidx.compose.runtime.Composable
import kotlin.io.path.Path


@Composable
actual fun BrowserPage(url: String, onBrowserClosed: () -> Unit) {
    try {
        val p: Process = Runtime.getRuntime()
            .exec(
                "${
                    Path(
                        System.getProperty("user.home"),
                        "AppData",
                        "Local",
                        "BraveSoftware",
                        "Brave-Browser",
                        "Application",
                        "brave.exe"
                    )
                } -incognito $url"
            )
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        onBrowserClosed()
    }
}

@Composable
actual fun LockScreenOrientation(orientation: Int) {
}