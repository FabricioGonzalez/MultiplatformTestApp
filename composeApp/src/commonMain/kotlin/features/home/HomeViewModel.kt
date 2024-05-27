package features.home

import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.screenModelScope
import presentation.model.ResourceUiState
import daniel.avila.rnm.kmm.presentation.mvi.BaseViewModel
import domain.interactors.videos.GetAllRecentVideosUsecase
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val loadRecentVideosUsecase: GetAllRecentVideosUsecase
) :
    BaseViewModel<HomeContract.Event, HomeContract.State, HomeContract.Effect>() {

    init {
        loadAllVideos()
    }

    override fun createInitialState(): HomeContract.State =
        HomeContract.State(
            videos = ResourceUiState.Idle,
            isFavorite = ResourceUiState.Idle,
        )

    override fun handleEvent(event: HomeContract.Event) {
        when (event) {
            HomeContract.Event.OnFavoriteClick -> switchCharacterFavorite(0)
            HomeContract.Event.OnTryCheckAgainClick -> loadAllVideos()
            HomeContract.Event.OnBackPressed -> setEffect { HomeContract.Effect.BackNavigation }
            is HomeContract.Event.OnVideoItemClicked -> {
                setEffect { HomeContract.Effect.NavigateToDetails(event.itemId) }
            }
        }
    }

    private fun loadAllVideos() {
        setState { copy(videos = ResourceUiState.Loading) }
        screenModelScope.launch {
            loadRecentVideosUsecase(Unit)
                .collect { result ->
                    result.onSuccess {
                        setState {
                            copy(videos = ResourceUiState.Success(flow { emit(it) }.cachedIn(screenModelScope)))
                        }
                    }
                        .onFailure {
                            setState { copy(videos = ResourceUiState.Error()) }
                        }
                }
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