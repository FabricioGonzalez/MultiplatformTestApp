package features.home

import androidx.paging.PagingData
import domain.model.VideoEntity
import kotlinx.coroutines.flow.Flow
import presentation.model.ResourceUiState
import presentation.mvi.UiEffect
import presentation.mvi.UiEvent
import presentation.mvi.UiState

interface HomeContract {
    sealed interface Event : UiEvent {
        data object OnTryCheckAgainClick : Event
        data object OnLoadDataRequested : Event
        data class OnVideoItemClicked(val itemId: String) : Event
        data object OnBackPressed : Event
    }

    data class State(
        val videoFeeds: ResourceUiState<List<VideoFeed>>,
    ) : UiState

    sealed interface Effect : UiEffect {
        data object CharacterAdded : Effect
        data object CharacterRemoved : Effect
        data object BackNavigation : Effect
        data class NavigateToDetails(val id: String) : Effect
    }
}

data class VideoFeed(
    val title: String,
    val videos: ResourceUiState<Flow<PagingData<VideoEntity>>>,
)