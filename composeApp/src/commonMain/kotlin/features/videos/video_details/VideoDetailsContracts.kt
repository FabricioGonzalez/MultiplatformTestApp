package features.videos.video_details

import domain.model.VideoDetailsEntity
import presentation.model.ResourceUiState
import presentation.mvi.UiEffect
import presentation.mvi.UiEvent
import presentation.mvi.UiState

interface VideoDetailsContracts {
    sealed interface Event : UiEvent {

        data class OnLoadDataRequested(val videoId: String) : Event
        data object OnBackPressed : Event
        data class OnNavigateToActressPressed(val id: String) : Event
        data class OnPlayVideoPressed(val videoId: String) : Event
        data class OnTagFavoritedChanged(val tag: String) : Event
    }

    data class State(
        val video: ResourceUiState<VideoDetailsEntity>,
        val isFavorite: ResourceUiState<Boolean>,
    ) : UiState

    sealed interface Effect : UiEffect {
        data object CharacterAdded : Effect
        data object CharacterRemoved : Effect
        data object BackNavigation : Effect
        data class PlayVideoRequested(val url: String) : Effect
        data class NavigateToActressesRequested(val id: String) : Effect
    }
}