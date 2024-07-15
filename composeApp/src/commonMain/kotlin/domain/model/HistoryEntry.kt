package domain.model

import kotlinx.datetime.LocalDateTime

data class HistoryEntry(
    val id: String,
    val videoTitle: String,
    val image: String?,
    val watchedOn: LocalDateTime
)
