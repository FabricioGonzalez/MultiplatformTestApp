package features.videos.video_details

import domain.interactors.tags.FavoriteTagUsecase
import domain.interactors.video_history.WriteToVideoHistoryUsecase
import domain.interactors.videos.GetVideoDetailsUsecase
import domain.model.VideoDetailsEntity
import domain.model.inputs.TagFavoriteInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.dsl.updateState
import pro.respawn.flowmvi.plugins.loggingPlugin
import pro.respawn.flowmvi.plugins.reduce
import pro.respawn.flowmvi.util.withType

class VideoDetailsStore(
    private val videoDetailsUsecase: GetVideoDetailsUsecase,
    private val favoriteTagUsecase: FavoriteTagUsecase,
    private val writeToVideoHistoryUsecase: WriteToVideoHistoryUsecase
) : Container<VideoDetailsState, VideoDetailsIntents, VideoDetailsEffect> {
    private val scope = CoroutineScope(Dispatchers.Default)

    override val store: Store<VideoDetailsState, VideoDetailsIntents, VideoDetailsEffect> = store(
        VideoDetailsState.Empty
    ) {
        install(loggingPlugin())

        reduce { state ->

            when (state) {
                is VideoDetailsIntents.OnLoadDataRequested -> {
                    updateState { VideoDetailsState.Loading }
                    scope.launch {
                        videoDetailsUsecase(state.videoId)
                            .onSuccess { succ ->
                                succ?.let {
                                    updateState {
                                        VideoDetailsState.Success(
                                            video = succ,
                                            isFavorite = false
                                        )
                                    }
                                }
                                    ?: updateState { VideoDetailsState.Error(message = "Nada foi encontrado") }
                            }.onFailure {
                                updateState {
                                    VideoDetailsState.Error(
                                        message = it.message ?: "Erro indescritivel"
                                    )
                                }
                            }
                    }
                }

                VideoDetailsIntents.OnBackPressed -> action(VideoDetailsEffect.BackNavigation)
                is VideoDetailsIntents.OnNavigateToActressPressed -> action(
                    VideoDetailsEffect.NavigateToActressesRequested(
                        state.id
                    )
                )


                is VideoDetailsIntents.OnPlayVideoPressed -> {
                    withState {
                        withType<VideoDetailsState.Success, VideoDetailsState>
                        {
                            writeToHistory(state.videoId)
                            this
                        }
                    }
                }

                is VideoDetailsIntents.OnTagFavoritedChanged -> updateState<VideoDetailsState.Success, _> {
                    copy(
                        video = video.copy(tags = video.tags.map {
                            if (it.name == state.tag) {
                                checkIfIsFavorite(it.name, !it.isFavorite)
                                it.copy(isFavorite = !it.isFavorite)
                            } else it
                        })
                    )
                }

            }
        }

    }

    private fun VideoDetailsState.Success.writeToHistory(videoId: String) {
        scope.launch {
            writeToVideoHistoryUsecase(video)
        }
    }


    private fun checkIfIsFavorite(tagName: String, isFavorite: Boolean) {
        scope.launch {
            favoriteTagUsecase(TagFavoriteInput(name = tagName, isFavorite = isFavorite))
        }
    }
}


sealed interface VideoDetailsIntents : MVIIntent {

    data class OnLoadDataRequested(val videoId: String) : VideoDetailsIntents
    data object OnBackPressed : VideoDetailsIntents
    data class OnNavigateToActressPressed(val id: String) : VideoDetailsIntents
    data class OnPlayVideoPressed(val videoId: String) : VideoDetailsIntents
    data class OnTagFavoritedChanged(val tag: String) : VideoDetailsIntents
}

sealed interface VideoDetailsState : MVIState {

    data class Success(
        val video: VideoDetailsEntity,
        val isFavorite: Boolean,
    ) : VideoDetailsState

    data class Error(
        val message: String
    ) : VideoDetailsState

    data object Loading : VideoDetailsState
    data object Empty : VideoDetailsState
}


sealed interface VideoDetailsEffect : MVIAction {
    data object CharacterAdded : VideoDetailsEffect
    data object CharacterRemoved : VideoDetailsEffect
    data object BackNavigation : VideoDetailsEffect
    data class PlayVideoRequested(val url: String) : VideoDetailsEffect
    data class NavigateToActressesRequested(val id: String) : VideoDetailsEffect
}