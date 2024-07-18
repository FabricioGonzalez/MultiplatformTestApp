package com.dev.fabricio.gonzalez.mediaapp.preview

import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import features.home.components.VideosListFeed
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
fun PreviewFeed() {
    Surface {
        val size = calculateWindowSizeClass()

        VideosListFeed(windowSizeClass = size,
            videos = listOf(

            ),
            onSweetsSelected = { /*setEvent(HomeContract.Event.OnVideoItemClicked(it.id)) */ })
    }
}