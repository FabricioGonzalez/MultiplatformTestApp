package domain.model

data class ActressEntity(
    val id:String,
    val name:String,
    val photo:String,
    val isFavorite:Boolean = false
) 
