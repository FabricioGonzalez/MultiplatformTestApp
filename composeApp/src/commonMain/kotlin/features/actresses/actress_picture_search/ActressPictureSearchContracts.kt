package features.actresses.actress_picture_search

import domain.model.VideoDetailsEntity
import presentation.model.ResourceUiState
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

interface ActressPictureSearchContracts {
    sealed interface Event : MVIIntent {

        data object OnBackPressed : Event
        data class OnNavigateToActressPressed(val id: String) : Event
        data class OnPlayVideoPressed(val url: String) : Event
    }

    data class State(
        val video: ResourceUiState<VideoDetailsEntity>,
        val isFavorite: ResourceUiState<Boolean>,
    ) : MVIState

    sealed interface Effect : MVIAction {
        data object CharacterAdded : Effect
        data object CharacterRemoved : Effect
        data object BackNavigation : Effect
        data class PlayVideoRequested(val url: String) : Effect
        data class NavigateToActressesRequested(val id: String) : Effect
    }
}