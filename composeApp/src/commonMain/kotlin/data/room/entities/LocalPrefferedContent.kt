package data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import domain.model.enums.ContentPreferrence

@Entity(tableName = "preffered_contents")
data class LocalPrefferedContent(
    @PrimaryKey
    val id: String,
    val label: String,
    val type: ContentPreferrence
)
