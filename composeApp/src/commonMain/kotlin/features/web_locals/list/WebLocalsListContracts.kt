package features.web_locals.list

import features.web_locals.list.components.Site
import presentation.model.ResourceUiState
import presentation.mvi.UiEffect
import presentation.mvi.UiEvent
import presentation.mvi.UiState

interface WebLocalsListContracts {
    sealed interface Event : UiEvent {

        data object OnLoadDataRequested : Event
        data object OnBackPressed : Event
        data class OnDeleteRequested(val id: String) : Event
        data class NavigateToWebLocalRequested(val id: String) : Event
        data class NavigateToWebLocalDetailsRequested(val id: String) : Event
    }

    data class State(
        val sites: ResourceUiState<List<Site>>,
        val isFavorite: ResourceUiState<Boolean>,
    ) : UiState

    sealed interface Effect : UiEffect {
        data object BackNavigation : Effect
        data class NavigateToWebLocalRequested(val id: String) : Effect
        data class NavigateToWebLocalDetailsRequested(val id: String) : Effect
    }
}