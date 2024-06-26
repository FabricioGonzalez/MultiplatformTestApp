package features.actresses.actresses_list

import androidx.paging.PagingData
import domain.model.ActressEntity
import kotlinx.coroutines.flow.Flow
import presentation.model.ResourceUiState
import presentation.mvi.UiEffect
import presentation.mvi.UiEvent
import presentation.mvi.UiState

interface ActressesListContracts {
    sealed interface Event : UiEvent {

        data object OnBackPressed : Event
        data class OnNavigateToActressDetailRequested(val actressId: String) : Event
    }

    data class State(
        val actresses: ResourceUiState<Flow<PagingData<ActressEntity>>>,
    ) : UiState

    sealed interface Effect : UiEffect {
        data object CharacterAdded : Effect
        data object CharacterRemoved : Effect
        data class ActressDetailNavigation(val actressId: String) : Effect
        data object BackNavigation : Effect
    }
}