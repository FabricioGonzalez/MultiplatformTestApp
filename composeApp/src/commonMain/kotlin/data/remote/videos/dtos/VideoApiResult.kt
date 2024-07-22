package data.remote.videos.dtos

import domain.model.VideoEntity


data class VideoApiResult(
    val startCursor: String?, val endCursor: String?, val videos: List<VideoEntity>
)