package data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "actresses")
data class LocalActress(
    @PrimaryKey
    val id: String,
    val name: String,
    val photo: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
)
