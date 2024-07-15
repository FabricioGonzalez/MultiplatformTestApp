package multiplatform

import androidx.compose.runtime.Composable

@Composable
actual fun BrowserPage(url: String, onBrowserClosed: () -> Unit) {
}


@Composable
actual fun LockScreenOrientation(orientation: Int) {
}