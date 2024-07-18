package data.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(
    tableName = "", foreignKeys =
    [ForeignKey(
        entity = LocalVideo::class,
        childColumns = ["videoId"],
        parentColumns = ["id"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index(
        unique = true,
        name = "ix_videoId",
        orders = [Index.Order.DESC],
        value = ["videoId"]
    ),
        Index(
            unique = false,
            name = "ix_watchedAt",
            orders = [Index.Order.DESC],
            value = ["watchedAt"]
        )]
)
data class LocalVideoHistory(
    @PrimaryKey
    val id: String,
    val videoId: String,
    val watchedAt: LocalDateTime
)
