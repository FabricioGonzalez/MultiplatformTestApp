package domain.model

data class PlayerEntity(
    val id:String,
    val playerLink:String,
    val postedAt:String,
    val isWorking:Boolean = true
)
