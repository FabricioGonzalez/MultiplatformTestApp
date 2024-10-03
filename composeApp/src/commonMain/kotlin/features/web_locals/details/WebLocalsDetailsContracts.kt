package features.web_locals.details

import features.web_locals.list.components.Site
import presentation.model.ResourceUiState
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

interface WebLocalsDetailsContracts {
    sealed interface Event : MVIIntent {

        data class OnLoadDataRequested(val webLocalId: String) : Event
        data class OnNameChanged(val name: String) : Event
        data class OnUrlChanged(val url: String) : Event
        data object OnSaveRequested : Event
        data object OnBackPressed : Event
    }

    data class State(
        val site: ResourceUiState<Site>,
        val isFavorite: ResourceUiState<Boolean>,
    ) : MVIState

    sealed interface Effect : MVIAction {
        data object BackNavigation : Effect
        data class NavigateToActressesRequested(val webLocalId: String) : Effect
    }
}