package features.web_locals.list

import features.web_locals.list.components.Site
import presentation.model.ResourceUiState
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

interface WebLocalsListContracts {
    sealed interface Event : MVIIntent {

        data object OnLoadDataRequested : Event
        data object OnBackPressed : Event
        data class OnDeleteRequested(val id: String) : Event
        data class NavigateToWebLocalRequested(val id: String) : Event
        data class NavigateToWebLocalDetailsRequested(val id: String) : Event
    }

    data class State(
        val sites: ResourceUiState<List<Site>>,
        val isFavorite: ResourceUiState<Boolean>,
    ) : MVIState

    sealed interface Effect : MVIAction {
        data object BackNavigation : Effect
        data class NavigateToWebLocalRequested(val id: String) : Effect
        data class NavigateToWebLocalDetailsRequested(val id: String) : Effect
    }
}