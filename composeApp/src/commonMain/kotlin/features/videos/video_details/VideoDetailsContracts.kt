package features.videos.video_details

import presentation.model.ResourceUiState
import daniel.avila.rnm.kmm.presentation.mvi.UiEffect
import daniel.avila.rnm.kmm.presentation.mvi.UiEvent
import daniel.avila.rnm.kmm.presentation.mvi.UiState
import domain.model.VideoDetailsEntity

interface VideoDetailsContracts {
    sealed interface Event : UiEvent {

        data object OnBackPressed : Event
    }

    data class State(
        val video: ResourceUiState<VideoDetailsEntity>,
        val isFavorite: ResourceUiState<Boolean>,
    ) : UiState

    sealed interface Effect : UiEffect {
        data object CharacterAdded : Effect
        data object CharacterRemoved : Effect
        data object BackNavigation : Effect
    }
}