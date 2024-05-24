package domain.model

data class VideoEntity(
    val cursor: String? = null,
    val id: String,
    val title: String,
    val photo: String
)
