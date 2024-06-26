package domain.model

import domain.model.enums.ContentPreferrence

data class PreferredContentEntity(
    val title: String,
    val type: ContentPreferrence
)
