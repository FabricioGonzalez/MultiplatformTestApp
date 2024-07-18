/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package features.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import com.example.feedcompose.ui.components.feed.Feed
import com.example.feedcompose.ui.components.feed.footer
import com.example.feedcompose.ui.components.feed.title
import components.ImageBox
import components.shimmerEffect
import domain.model.VideoEntity
import features.home.data.VideoFeed
import kotlinx.coroutines.launch
import presentation.ui.common.state.ManagementResourceUiState

@Composable
internal fun VideosListFeed(
    windowSizeClass: WindowSizeClass, videos: List<VideoFeed>, onSweetsSelected: (VideoEntity) -> Unit = {}
) {
    val state = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    Feed(
        columns = rememberColumns(windowSizeClass = windowSizeClass),
        state = state,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        videos.forEach { videoList ->
            title(contentType = "feed-title") {
                SectionTitle(text = videoList.title)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                ManagementResourceUiState(
                    modifier = Modifier,
                    resourceUiState = videoList.videos,
                    successView = { videos ->
                        val items = videos.collectAsLazyPagingItems()
                        LazyHorizontalGrid(
                            rows = rememberRows(windowSizeClass), modifier = Modifier.fillMaxWidth().height(
                                when (windowSizeClass.heightSizeClass) {
                                    WindowHeightSizeClass.Compact -> 172.dp
                                    WindowHeightSizeClass.Medium -> 392.dp
                                    WindowHeightSizeClass.Expanded -> 744.dp
                                    else -> 744.dp
                                }
                            )
                        ) {
                            items(items.itemCount,
                                contentType = { "sweets" },
                                key = { items[it]!!.id }) {
                                SquareSweetsCard(
                                    sweets = items[it]!!,
                                    modifier = Modifier.width(
                                        when (windowSizeClass.widthSizeClass) {
                                            WindowWidthSizeClass.Compact -> 172.dp
                                            WindowWidthSizeClass.Medium -> 200.dp
                                            WindowWidthSizeClass.Expanded -> 248.dp
                                            else -> 248.dp
                                        }
                                    ).height(
                                        when (windowSizeClass.heightSizeClass) {
                                            WindowHeightSizeClass.Compact -> 172.dp
                                            WindowHeightSizeClass.Medium -> 200.dp
                                            WindowHeightSizeClass.Expanded -> 248.dp
                                            else -> 248.dp
                                        }
                                    ),
                                    isLoading = items.loadState.refresh is LoadState.Loading,
                                    onClick = onSweetsSelected
                                )
                            }
                        }
                    },
                    onTryAgain = { },
                    onCheckAgain = { },
                )
            }
        }

        footer {
            BackToTopButton(modifier = Modifier.padding(PaddingValues(top = 32.dp))) {
                coroutineScope.launch {
                    state.animateScrollToItem(0)
                }
            }
        }
    }
}

@Composable
fun rememberColumns(windowSizeClass: WindowSizeClass, preferredSize: Int = 1) = remember(windowSizeClass) {
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> GridCells.Fixed(preferredSize)
        WindowWidthSizeClass.Medium -> GridCells.Fixed(preferredSize + 1)
        WindowWidthSizeClass.Expanded -> GridCells.Fixed(preferredSize + 2)
        else -> GridCells.Fixed(preferredSize + 3)
    }
}

@Composable
fun rememberRows(windowSizeClass: WindowSizeClass, preferredSize: Int = 1) = remember(windowSizeClass) {
    when (windowSizeClass.heightSizeClass) {
        WindowHeightSizeClass.Compact -> GridCells.Fixed(preferredSize)
        WindowHeightSizeClass.Medium -> GridCells.Fixed(preferredSize + 1)
        WindowHeightSizeClass.Expanded -> GridCells.Fixed(preferredSize + 2)
        else -> GridCells.Fixed(preferredSize + 3)
    }
}

@Composable
private fun FeedTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.padding(PaddingValues(vertical = 24.dp))
    )
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(PaddingValues(top = 32.dp, bottom = 8.dp))
    )
}

@Composable
private fun HorizontalSweetsList(
    sweets: List<VideoEntity>, cardWidth: Dp, onSweetsSelected: (VideoEntity) -> Unit = {}
) {
    LazyRow(
        modifier = Modifier.padding(PaddingValues(bottom = 16.dp)), horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sweets.size, key = { sweets[it].id }, contentType = { "sweets" }) {
            PortraitSweetsCard(
                sweets = sweets[it], onClick = onSweetsSelected, modifier = Modifier.width(cardWidth), isLoading = false
            )
        }
    }
}

@Composable
private fun FilterSelector(selectedFilter: Filter, onFilterSelected: (Filter) -> Unit) {
    val filters = listOf(
        Filter.All to "", Filter.Candy to "", Filter.Pastry to ""
    )
    filters.forEach { (filter, labelId) ->
        val selected = selectedFilter == filter
        FilterChip(selected = selected,
            onClick = { onFilterSelected(filter) },
            label = { Text(text = labelId) },
            leadingIcon = {
                if (selected) {
                    Icon(
                        Icons.Rounded.Check, contentDescription = null
                    )
                }
            })
    }
}

@Composable
private fun BackToTopButton(modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Box(contentAlignment = Alignment.Center) {
        Button(onClick = onClick, modifier = modifier) {
            Icon(Icons.Rounded.KeyboardArrowUp, null)
            Text(text = "Back to top")
        }
    }
}

@Composable
fun SquareSweetsCard(
    sweets: VideoEntity, modifier: Modifier = Modifier, onClick: (VideoEntity) -> Unit = {}, isLoading: Boolean
) {
    SweetsCard(
        sweets = sweets, modifier = modifier/*.aspectRatio(1f)*/, isLoading = isLoading, onClick = onClick
    )
}

@Composable
fun PortraitSweetsCard(
    sweets: VideoEntity, modifier: Modifier = Modifier, onClick: (VideoEntity) -> Unit = {}, isLoading: Boolean
) {
    SweetsCard(
        sweets = sweets, modifier = modifier.aspectRatio(0.707f), onClick = onClick, isLoading = isLoading
    )
}

@Composable
fun SweetsCard(
    sweets: VideoEntity, modifier: Modifier = Modifier, onClick: (VideoEntity) -> Unit = {}, isLoading: Boolean
) {
    var isFocused by remember {
        mutableStateOf(false)
    }
    val outlineColor = if (isFocused) {
        MaterialTheme.colorScheme.outline
    } else {
        MaterialTheme.colorScheme.background
    }

    Card(modifier = modifier.onFocusChanged {
        isFocused = it.isFocused
    }.border(width = 2.dp, color = outlineColor).clip(MaterialTheme.shapes.medium),
        onClick = { onClick(sweets) }) {
        Box(modifier = Modifier.fillMaxSize()) {
            ImageBox(
                modifier = Modifier.fillMaxSize().then(if (isLoading) Modifier.shimmerEffect() else Modifier),
                photo = sweets.photo,
            )
            Text(
                text = sweets.title,
                modifier = Modifier.fillMaxWidth().zIndex(2f)
                    .then(if (isLoading) Modifier.shimmerEffect() else Modifier).align(Alignment.BottomCenter)
                    .background(MaterialTheme.colorScheme.primary.copy(0.5f)).padding(8.dp, 4.dp),
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onPrimary)
            )
        }

    }
}

sealed class Filter(private val categories: List<String>) {
    fun apply(sweets: VideoEntity): Boolean = categories.indexOf(sweets.cursor) != -1

    data object All : Filter(listOf("", ""))

    data object Candy : Filter(listOf(""))

    data object Pastry : Filter(listOf(""))
}
