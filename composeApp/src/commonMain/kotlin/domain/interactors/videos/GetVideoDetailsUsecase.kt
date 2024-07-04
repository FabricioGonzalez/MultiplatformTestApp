package domain.interactors.videos

import data.repositories.VideoRepository
import domain.interactors.type.BaseUseCase
import domain.model.VideoDetailsEntity
import kotlinx.coroutines.CoroutineDispatcher

class GetVideoDetailsUsecase(
    private val repository: VideoRepository,
    dispatcher: CoroutineDispatcher,
) : BaseUseCase<String, VideoDetailsEntity?>(dispatcher) {
    override suspend fun block(param: String): VideoDetailsEntity? {
        return repository.getVideoDetails(param)
            .fold(onSuccess = {
                it
            }, onFailure = {
                println(it)
                null
            })
    }
}