package data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(
    tableName = "videos"
)
data class LocalVideo(
    @PrimaryKey val id: String,
    val title: String,
    val photo: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val originalCreationDate: LocalDateTime,
)
