package features.web_locals.details

import features.web_locals.list.components.Site
import presentation.model.ResourceUiState
import presentation.mvi.UiEffect
import presentation.mvi.UiEvent
import presentation.mvi.UiState

interface WebLocalsDetailsContracts {
    sealed interface Event : UiEvent {

        data class OnLoadDataRequested(val webLocalId: String) : Event
        data class OnNameChanged(val name: String) : Event
        data class OnUrlChanged(val url: String) : Event
        data object OnSaveRequested : Event
        data object OnBackPressed : Event
    }

    data class State(
        val site: ResourceUiState<Site>,
        val isFavorite: ResourceUiState<Boolean>,
    ) : UiState

    sealed interface Effect : UiEffect {
        data object BackNavigation : Effect
        data class NavigateToActressesRequested(val webLocalId: String) : Effect
    }
}