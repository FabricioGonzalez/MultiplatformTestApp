package multiplatform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform