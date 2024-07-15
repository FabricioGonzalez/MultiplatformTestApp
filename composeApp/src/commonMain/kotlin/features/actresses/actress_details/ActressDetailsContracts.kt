package features.actresses.actress_details

import androidx.paging.PagingData
import domain.model.ActressEntity
import domain.model.VideoEntity
import kotlinx.coroutines.flow.Flow
import presentation.model.ResourceUiState
import presentation.mvi.UiEffect
import presentation.mvi.UiEvent
import presentation.mvi.UiState

interface ActressDetailsContracts {
    sealed interface Event : UiEvent {

        data class OnActressPhotoRequested(val actressName: String) : Event
        data class OnLoadDataRequested(val id: String) : Event
        data class OnActressFavorited(val isFavorite: Boolean) : Event
        data class OnEditRequested(val isEditing: Boolean) : Event
        data class OnVideoItemClicked(val id: String) : Event
        data class OnNameChanged(val name: String) : Event
        data class OnPhotoChanged(val photo: String) : Event
        data object OnBackPressed : Event
    }

    data class State(
        val actress: ResourceUiState<ActressEntity>,
        val videos: ResourceUiState<Flow<PagingData<VideoEntity>>>,
        val isFavorite: Boolean = false,
        val isEditing: Boolean = false,
    ) : UiState


    sealed interface Effect : UiEffect {
        data object CharacterAdded : Effect
        data object CharacterRemoved : Effect
        data class OnVideoItemClicked(val videoId: String) : Effect
        data class OnActressPhotoRequested(val actressName: String) : Effect
        data object BackNavigation : Effect
    }
}