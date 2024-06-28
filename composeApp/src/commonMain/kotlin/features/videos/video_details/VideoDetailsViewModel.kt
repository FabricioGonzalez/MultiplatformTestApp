package features.videos.video_details

import cafe.adriel.voyager.core.model.screenModelScope
import domain.interactors.tags.FavoriteTagUsecase
import domain.interactors.videos.GetVideoDetailsUsecase
import domain.interactors.videos.WriteToVideoHistoryUsecase
import domain.model.VideoDetailsEntity
import domain.model.inputs.TagFavoriteInput
import kotlinx.coroutines.launch
import presentation.model.ResourceUiState
import presentation.mvi.BaseViewModel

class VideoDetailsViewModel(
    private val videoDetailsUsecase: GetVideoDetailsUsecase,
    private val favoriteTagUsecase: FavoriteTagUsecase,
    private val writeToVideoHistoryUsecase: WriteToVideoHistoryUsecase

) :
    BaseViewModel<VideoDetailsContracts.Event, VideoDetailsContracts.State, VideoDetailsContracts.Effect>() {

    override fun createInitialState(): VideoDetailsContracts.State = VideoDetailsContracts.State(
        video = ResourceUiState.Idle,
        isFavorite = ResourceUiState.Idle,
    )

    override fun handleEvent(event: VideoDetailsContracts.Event) {
        when (event) {
            is VideoDetailsContracts.Event.OnLoadDataRequested -> getDetails(event.videoId)
            VideoDetailsContracts.Event.OnBackPressed -> setEffect { VideoDetailsContracts.Effect.BackNavigation }
            is VideoDetailsContracts.Event.OnNavigateToActressPressed -> setEffect {
                VideoDetailsContracts.Effect.NavigateToActressesRequested(
                    event.id
                )
            }

            is VideoDetailsContracts.Event.OnPlayVideoPressed -> {
                writeToHistory(event.videoId)
            }

            is VideoDetailsContracts.Event.OnTagFavoritedChanged -> {
                if (uiState.value.video is ResourceUiState.Success<VideoDetailsEntity>) setState {
                    copy(
                        video = (uiState.value.video as ResourceUiState.Success<VideoDetailsEntity>).copy(
                            data = (uiState.value.video as ResourceUiState.Success<VideoDetailsEntity>).data.copy(
                                tags = (uiState.value.video as ResourceUiState.Success<VideoDetailsEntity>).data.tags.map {
                                    if (it.name == event.tag) {
                                        checkIfIsFavorite(it.name, !it.isFavorite)
                                        it.copy(isFavorite = !it.isFavorite)
                                    } else it
                                })
                        )
                    )
                }
            }
        }
    }

    private fun writeToHistory(videoId: String) {
        screenModelScope.launch {
            writeToVideoHistoryUsecase(videoId)
        }
    }

    private fun getDetails(id: String) {
        setState { copy(video = ResourceUiState.Loading) }
        screenModelScope.launch {
            videoDetailsUsecase(id).onSuccess { succ ->
                succ?.let { setState { copy(video = ResourceUiState.Success(succ)) } }
                    ?: setState { copy(video = ResourceUiState.Error()) }
            }.onFailure { setState { copy(video = ResourceUiState.Error()) } }
        }
    }

    private fun checkIfIsFavorite(tagName: String, isFavorite: Boolean) {
        screenModelScope.launch {
            favoriteTagUsecase(TagFavoriteInput(name = tagName, isFavorite = isFavorite))
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