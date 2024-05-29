package features.actresses.actress_details

import cafe.adriel.voyager.core.model.screenModelScope
import presentation.model.ResourceUiState
import daniel.avila.rnm.kmm.presentation.mvi.BaseViewModel
import domain.interactors.videos.GetVideoDetailsUsecase
import features.videos.video_details.VideoDetailsContracts
import kotlinx.coroutines.launch

class ActressDetailsViewModel(
    actressId: String,
    private val videoDetailsUsecase: GetVideoDetailsUsecase
) : BaseViewModel<VideoDetailsContracts.Event, VideoDetailsContracts.State, VideoDetailsContracts.Effect>() {

    init {
        getDetails(actressId)
    }

    override fun createInitialState(): VideoDetailsContracts.State =
        VideoDetailsContracts.State(
            video = ResourceUiState.Idle,
            isFavorite = ResourceUiState.Idle,
        )

    override fun handleEvent(event: VideoDetailsContracts.Event) {
        when (event) {
            VideoDetailsContracts.Event.OnBackPressed -> setEffect { VideoDetailsContracts.Effect.BackNavigation }
        }
    }

    private fun getDetails(id: String) {
        setState { copy(video = ResourceUiState.Loading) }
        screenModelScope.launch {
            videoDetailsUsecase(id)
                .onSuccess { succ ->
                    succ?.let { setState { copy(video = ResourceUiState.Success(succ)) } }
                        ?: setState { copy(video = ResourceUiState.Error()) }
                }
                .onFailure { setState { copy(video = ResourceUiState.Error()) } }
        }
    }

    private fun checkIfIsFavorite(idCharacter: Int) {
        setState { copy(isFavorite = ResourceUiState.Loading) }
        screenModelScope.launch {
            /*isCharacterFavoriteUseCase(idCharacter)
                .onSuccess { setState { copy(isFavorite = ResourceUiState.Success(it)) } }
                .onFailure { setState { copy(isFavorite = ResourceUiState.Error()) } }*/
        }
    }

    private fun switchCharacterFavorite(idCharacter: Int) {
        setState { copy(isFavorite = ResourceUiState.Loading) }
        screenModelScope.launch {
            /*switchCharacterFavoriteUseCase(idCharacter)
                .onSuccess {
                    setState { copy(isFavorite = ResourceUiState.Success(it)) }
                    setEffect { VideoDetailsContracts.Effect.CharacterAdded }
                }.onFailure { setState { copy(isFavorite = ResourceUiState.Error()) } }*/
        }
    }
}