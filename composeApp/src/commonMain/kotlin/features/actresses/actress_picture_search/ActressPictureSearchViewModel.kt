package features.actresses.actress_picture_search

import cafe.adriel.voyager.core.model.screenModelScope
import domain.interactors.videos.GetVideoDetailsUsecase
import kotlinx.coroutines.launch
import presentation.model.ResourceUiState
import presentation.mvi.BaseViewModel

class ActressPictureSearchViewModel(
    private val videoDetailsUsecase: GetVideoDetailsUsecase
) : BaseViewModel<ActressPictureSearchContracts.Event, ActressPictureSearchContracts.State, ActressPictureSearchContracts.Effect>() {

    override fun createInitialState(): ActressPictureSearchContracts.State =
        ActressPictureSearchContracts.State(
            video = ResourceUiState.Idle,
            isFavorite = ResourceUiState.Idle,
        )

    override fun handleEvent(event: ActressPictureSearchContracts.Event) {
        when (event) {
            ActressPictureSearchContracts.Event.OnBackPressed -> setEffect { ActressPictureSearchContracts.Effect.BackNavigation }
            is ActressPictureSearchContracts.Event.OnNavigateToActressPressed -> setEffect {
                ActressPictureSearchContracts.Effect.NavigateToActressesRequested(
                    event.id
                )
            }

            is ActressPictureSearchContracts.Event.OnPlayVideoPressed -> setEffect {
                ActressPictureSearchContracts.Effect.PlayVideoRequested(
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
                    setEffect { WebLocalsListContracts.Effect.CharacterAdded }
                }.onFailure { setState { copy(isFavorite = ResourceUiState.Error()) } }*/
        }
    }
}