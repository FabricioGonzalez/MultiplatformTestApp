package domain.repositories

import domain.model.inputs.TagFavoriteInput

interface TagRepository {
    suspend fun favoriteTag(input: TagFavoriteInput)
}