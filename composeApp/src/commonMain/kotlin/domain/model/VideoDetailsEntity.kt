package domain.model

data class VideoDetailsEntity(
    val id: String,
    val title: String,
    val photo: String,
    val createdAt: String,
    val addedToAt: String,
    val actresses: List<ActressEntity>,
    val tags: List<TagEntity>,
    val players: List<PlayerEntity>
)