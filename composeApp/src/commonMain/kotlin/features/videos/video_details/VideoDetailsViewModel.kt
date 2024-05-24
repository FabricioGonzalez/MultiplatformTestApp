package features.videos.video_details

import cafe.adriel.voyager.core.model.screenModelScope
import daniel.avila.rnm.kmm.presentation.model.ResourceUiState
import daniel.avila.rnm.kmm.presentation.mvi.BaseViewModel
import kotlinx.coroutines.launch

class VideoDetailsViewModel(
    private val videoId: Int,
) : BaseViewModel<VideoDetailsContracts.Event, VideoDetailsContracts.State, VideoDetailsContracts.Effect>() {

    init {
        getCharacter(videoId)
        checkIfIsFavorite(videoId)
    }

    override fun createInitialState(): VideoDetailsContracts.State =
        VideoDetailsContracts.State(
            video = ResourceUiState.Idle,
            isFavorite = ResourceUiState.Idle,
        )

    override fun handleEvent(event: VideoDetailsContracts.Event) {
        when (event) {
            VideoDetailsContracts.Event.OnFavoriteClick -> switchCharacterFavorite(videoId)
            VideoDetailsContracts.Event.OnTryCheckAgainClick -> getCharacter(videoId)
            VideoDetailsContracts.Event.OnBackPressed -> setEffect { VideoDetailsContracts.Effect.BackNavigation }
        }
    }

    private fun getCharacter(characterId: Int) {
        setState { copy(video = ResourceUiState.Loading) }
        screenModelScope.launch {
            /* getCharacterUseCase(characterId)
                 .onSuccess { setState { copy(video = ResourceUiState.Success(it)) } }
                 .onFailure { setState { copy(video = ResourceUiState.Error()) } }*/
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