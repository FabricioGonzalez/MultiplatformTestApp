package domain.model

import kotlinx.datetime.LocalDateTime

data class VideoDetailsEntity(
    val id: String,
    val title: String,
    val photo: String,
    val createdAt: LocalDateTime,
    val addedToAt: LocalDateTime?,
    val actresses: List<ActressEntity>,
    val tags: List<TagEntity>,
    val players: List<PlayerEntity>
)