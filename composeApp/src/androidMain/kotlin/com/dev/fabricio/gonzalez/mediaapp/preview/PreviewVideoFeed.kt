package com.dev.fabricio.gonzalez.mediaapp.preview

import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import features.home.components.VideosListFeed
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun PreviewFeed() {
    Surface {
        val size = currentWindowAdaptiveInfo().windowSizeClass

        VideosListFeed(windowSizeClass = size,
            videos = listOf(

            ),
            onSweetsSelected = { /*setEvent(HomeContract.Event.OnVideoItemClicked(it.id)) */ })
    }
}