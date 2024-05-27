package domain.model

data class TagEntity(
    val id: String,
    val name: String,
    val isFavorite: Boolean = false
)