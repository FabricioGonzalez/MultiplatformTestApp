import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.application
import com.dev.fabricio.gonzalez.composeapp.generated.resources.Res
import com.dev.fabricio.gonzalez.composeapp.generated.resources.ic_launcher_icon
import com.materialkolor.rememberDynamicColorScheme
import dev.datlag.kcef.KCEF
import di.initKoin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.styling.DecoratedWindowStyle
import org.jetbrains.jewel.window.styling.TitleBarStyle
import java.awt.Dimension
import java.io.File

fun main() {
    val paletteGeneration = PaletteGeneration()

    initKoin {

    }

    application {
        val os = System.getProperty("os.name")

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                val localPath = File(System.getenv("APPDATA"), "MediaApp")

                KCEF.init(builder = {
                    installDir(File(localPath, "kcef-bundle"))
                    progress {
                        /* onDownloading {
                             downloading = max(it, 0F)
                         }
                         onInitialized {
                             initialized = true
                         }*/
                    }
                    settings {
                        cachePath = File(localPath, "cache").absolutePath
                    }
                }, onError = {
                    it?.printStackTrace()
                }, onRestartRequired = {
                    /*restartRequired = true*/
                })
            }
        }
        DisposableEffect(Unit) {
            onDispose {
                KCEF.disposeBlocking()
            }
        }

        IntUiTheme(
            JewelTheme.darkThemeDefinition(),
            ComponentStyling.decoratedWindow(
                titleBarStyle = TitleBarStyle.dark()
            ),
        ) {
            DecoratedWindow(
                onCloseRequest = ::exitApplication,
                title = "MediaApp",
                icon = painterResource(Res.drawable.ic_launcher_icon),
                style = DecoratedWindowStyle.dark()
            ) {

                window.minimumSize = Dimension(400, 450)

                var theme by remember { mutableStateOf(IntUiThemes.Dark) }
                val isDark by remember { derivedStateOf { theme == IntUiThemes.Dark } }

                TitleBarView(
                    theme = theme,
                    appBar = {},
                    changeTheme = {
                        theme = when (theme) {
                            IntUiThemes.Light -> IntUiThemes.Dark
                            IntUiThemes.Dark, IntUiThemes.System -> IntUiThemes.Light
                        }
                    }
                )
                val colorScheme = if (os.contains("Windows")) {
                    rememberDynamicColorScheme(Color(paletteGeneration.getAccentColor()), isDark)
                } else null

                App(
                    isDarkTheme = isDark, appColor = colorScheme
                )
            }
        }
    }
}