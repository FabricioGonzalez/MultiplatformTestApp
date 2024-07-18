package data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime


@Entity(tableName = "tags")
data class LocalTag(
    @PrimaryKey
    val id: String,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
)
