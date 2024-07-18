package data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "web_locals")
data class LocalWebLocal(
    @PrimaryKey
    val id: String,
    val title: String,
    val url: String
)
