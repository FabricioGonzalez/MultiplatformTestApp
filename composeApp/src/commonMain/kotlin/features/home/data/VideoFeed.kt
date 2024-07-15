package features.home.data

import androidx.paging.PagingData
import domain.model.VideoEntity
import kotlinx.coroutines.flow.Flow
import presentation.model.ResourceUiState

data class VideoFeed(
    val title: String,
    val videos: ResourceUiState<Flow<PagingData<VideoEntity>>>,
)