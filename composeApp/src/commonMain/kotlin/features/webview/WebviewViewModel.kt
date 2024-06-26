package features.webview

import cafe.adriel.voyager.core.model.screenModelScope
import domain.interactors.videos.GetVideoDetailsUsecase
import kotlinx.coroutines.launch
import presentation.model.ResourceUiState
import presentation.mvi.BaseViewModel

class WebviewViewModel(
    private val videoDetailsUsecase: GetVideoDetailsUsecase
) : BaseViewModel<WebviewContracts.Event, WebviewContracts.State, WebviewContracts.Effect>() {

    override fun createInitialState(): WebviewContracts.State =
        WebviewContracts.State(
            video = ResourceUiState.Idle,
            isFavorite = ResourceUiState.Idle,
        )

    override fun handleEvent(event: WebviewContracts.Event) {
        when (event) {
            WebviewContracts.Event.OnBackPressed -> setEffect { WebviewContracts.Effect.BackNavigation }
            is WebviewContracts.Event.OnNavigateToActressPressed -> setEffect {
                WebviewContracts.Effect.NavigateToActressesRequested(
                    event.id
                )
            }

            is WebviewContracts.Event.OnPlayVideoPressed -> setEffect {
                WebviewContracts.Effect.PlayVideoRequested(
                    event.url
                )
            }
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