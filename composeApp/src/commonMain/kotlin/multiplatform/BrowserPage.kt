package multiplatform

import androidx.compose.runtime.Composable

@Composable
expect fun BrowserPage(url: String, onBrowserClosed: () -> Unit)

@Composable
expect fun LockScreenOrientation(orientation: Int)