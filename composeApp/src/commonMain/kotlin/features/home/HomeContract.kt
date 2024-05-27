package features.home

import androidx.paging.PagingData
import presentation.model.ResourceUiState
import daniel.avila.rnm.kmm.presentation.mvi.UiEffect
import daniel.avila.rnm.kmm.presentation.mvi.UiEvent
import daniel.avila.rnm.kmm.presentation.mvi.UiState
import domain.model.VideoEntity
import kotlinx.coroutines.flow.Flow

interface HomeContract {
    sealed interface Event : UiEvent {
        data object OnFavoriteClick : Event
        data object OnTryCheckAgainClick : Event
        data class OnVideoItemClicked(val itemId: String) : Event
        data object OnBackPressed : Event
    }

    data class State(
        val videos: ResourceUiState<Flow<PagingData<VideoEntity>>>,
        val isFavorite: ResourceUiState<Boolean>,
    ) : UiState

    sealed interface Effect : UiEffect {
        data object CharacterAdded : Effect
        data object CharacterRemoved : Effect
        data object BackNavigation : Effect
        data class NavigateToDetails(val id:String) : Effect
    }
}