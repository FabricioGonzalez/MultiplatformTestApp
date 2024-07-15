package features.actresses.actress_details

import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.screenModelScope
import domain.interactors.actresses.FavoriteActressUsecase
import domain.interactors.actresses.GetActressDetailsUsecase
import domain.interactors.actresses.UpdateActressUsecase
import domain.interactors.videos.GetVideosByActressUsecase
import domain.model.ActressEntity
import domain.model.inputs.TagFavoriteInput
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import presentation.model.ResourceUiState
import presentation.mvi.BaseViewModel

class ActressDetailsViewModel(
    private val actressDetailsUsecase: GetActressDetailsUsecase,
    private val updateActressUsecase: UpdateActressUsecase,
    private val videosByActressUsecase: GetVideosByActressUsecase,
    private val favoriteActressUsecase: FavoriteActressUsecase,
) : BaseViewModel<ActressDetailsContracts.Event, ActressDetailsContracts.State, ActressDetailsContracts.Effect>() {


    override fun createInitialState(): ActressDetailsContracts.State =
        ActressDetailsContracts.State(
            videos = ResourceUiState.Idle,
            actress = ResourceUiState.Idle,
        )

    override fun handleEvent(event: ActressDetailsContracts.Event) {
        when (event) {
            is ActressDetailsContracts.Event.OnLoadDataRequested -> getDetails(event.id)
            ActressDetailsContracts.Event.OnBackPressed -> setEffect { ActressDetailsContracts.Effect.BackNavigation }
            is ActressDetailsContracts.Event.OnActressPhotoRequested -> setEffect {
                ActressDetailsContracts.Effect.OnActressPhotoRequested(
                    event.actressName
                )
            }

            is ActressDetailsContracts.Event.OnEditRequested -> {
                setState { copy(isEditing = event.isEditing) }

                if (!event.isEditing && uiState.value.actress is ResourceUiState.Success) {
                    mutateActress((uiState.value.actress as ResourceUiState.Success<ActressEntity>).data)
                }
            }

            is ActressDetailsContracts.Event.OnNameChanged -> {
                setState {
                    if (actress is ResourceUiState.Success<ActressEntity>) copy(
                        actress = actress.copy(
                            data = actress.data.copy(
                                name = event.name
                            )
                        )
                    ) else copy(actress = actress)
                }
            }

            is ActressDetailsContracts.Event.OnPhotoChanged -> {
                setState {
                    if (actress is ResourceUiState.Success<ActressEntity>) copy(
                        actress = actress.copy(
                            data = actress.data.copy(
                                photo = event.photo
                            )
                        )
                    ) else copy(actress = actress)
                }
            }

            is ActressDetailsContracts.Event.OnActressFavorited -> {
                setState { copy(isFavorite = event.isFavorite) }

                if (uiState.value.actress is ResourceUiState.Success) {
                    favoriteActress(
                        (uiState.value.actress as ResourceUiState.Success<ActressEntity>).data.name,
                        event.isFavorite
                    )
                }
            }

            is ActressDetailsContracts.Event.OnVideoItemClicked -> {
                setEffect { ActressDetailsContracts.Effect.OnVideoItemClicked(event.id) }
            }
        }
    }

    private fun mutateActress(actress: ActressEntity) {
        screenModelScope.launch {
            try {
                updateActressUsecase(actress)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun favoriteActress(name: String, isFavorite: Boolean) {
        screenModelScope.launch {
            try {
                favoriteActressUsecase(TagFavoriteInput(name, isFavorite))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getDetails(id: String) {
        setState { copy(actress = ResourceUiState.Loading) }
        screenModelScope.launch {
            actressDetailsUsecase(id)
                .onSuccess { succ ->
                    succ.getOrNull()?.let {
                        setState {
                            copy(
                                actress = ResourceUiState.Success(it),
                                isFavorite = it.isFavorite
                            )
                        }
                        videosByActressUsecase(it.name)
                            .collect { result ->
                                result.onSuccess {
                                    setState {
                                        copy(
                                            videos = ResourceUiState.Success(
                                                flow { emit(it) }.cachedIn(
                                                    screenModelScope
                                                )
                                            )
                                        )
                                    }
                                }
                                    .onFailure {
                                        setState { copy(videos = ResourceUiState.Error()) }
                                    }
                            }
                    }
                        ?: setState { copy(actress = ResourceUiState.Error()) }
                }
                .onFailure { setState { copy(actress = ResourceUiState.Error()) } }


        }
    }

    private fun checkIfIsFavorite(idCharacter: Int) {
        setState { copy(isFavorite = false) }
        screenModelScope.launch {
            /*isCharacterFavoriteUseCase(idCharacter)
                .onSuccess { setState { copy(isFavorite = ResourceUiState.Success(it)) } }
                .onFailure { setState { copy(isFavorite = ResourceUiState.Error()) } }*/
        }
    }

    private fun switchCharacterFavorite(idCharacter: Int) {
        setState { copy(isFavorite = false) }
        screenModelScope.launch {
            /*switchCharacterFavoriteUseCase(idCharacter)
                .onSuccess {
                    setState { copy(isFavorite = ResourceUiState.Success(it)) }
                    setEffect { VideoDetailsContracts.Effect.CharacterAdded }
                }.onFailure { setState { copy(isFavorite = ResourceUiState.Error()) } }*/
        }
    }
}