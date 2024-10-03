package features.actresses.actresses_list

import androidx.paging.PagingData
import domain.model.ActressEntity
import kotlinx.coroutines.flow.Flow
import presentation.model.ResourceUiState
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

interface ActressesListContracts {
    sealed interface Event : MVIIntent {

        data object OnBackPressed : Event
        data class OnNavigateToActressDetailRequested(val actressId: String) : Event
        data class OnSearchTextChanged(val searchText: String) : Event
    }

    data class State(
        val actresses: ResourceUiState<Flow<PagingData<ActressEntity>>>,
        val searchActresses: ResourceUiState<Flow<PagingData<ActressEntity>>>,
        val searchText: String,
    ) : MVIState

    sealed interface Effect : MVIAction {
        data object CharacterAdded : Effect
        data object CharacterRemoved : Effect
        data class ActressDetailNavigation(val actressId: String) : Effect
        data object BackNavigation : Effect
    }
}