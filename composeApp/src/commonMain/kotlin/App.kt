import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import features.home.HomeScreen
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview
import themes.MediaAppTheme

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App(isDarkTheme: Boolean = false) {
    MediaAppTheme(darkTheme = isDarkTheme) {
        Navigator(HomeScreen())
    }
}
