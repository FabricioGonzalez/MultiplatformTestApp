package features.actresses.actresses_list

import androidx.paging.PagingData
import presentation.model.ResourceUiState
import daniel.avila.rnm.kmm.presentation.mvi.UiEffect
import daniel.avila.rnm.kmm.presentation.mvi.UiEvent
import daniel.avila.rnm.kmm.presentation.mvi.UiState
import domain.model.ActressEntity
import domain.model.VideoDetailsEntity
import kotlinx.coroutines.flow.Flow

interface ActressesListContracts {
    sealed interface Event : UiEvent {

        data object OnBackPressed : Event
    }

    data class State(
        val actresses: ResourceUiState<Flow<PagingData<ActressEntity>>>,
    ) : UiState

    sealed interface Effect : UiEffect {
        data object CharacterAdded : Effect
        data object CharacterRemoved : Effect
        data object BackNavigation : Effect
    }
}