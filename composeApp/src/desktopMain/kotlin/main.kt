import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.window.application
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

fun main() {
    val paletteGeneration = PaletteGeneration()

    initKoin {}

    application {
        val os = System.getProperty("os.name")


        IntUiTheme(
            JewelTheme.darkThemeDefinition(),
            ComponentStyling.decoratedWindow(
                titleBarStyle = TitleBarStyle.dark()
            ),
        ) {
            DecoratedWindow(
                onCloseRequest = ::exitApplication,
                title = "MediaApp",
                icon = rememberVectorPainter(Icons.Filled.Warning),
                style = DecoratedWindowStyle.dark()
            ) {
                window.minimumSize = Dimension(720, 450)

                var theme by remember { mutableStateOf(IntUiThemes.Dark) }
                val isDark by remember { derivedStateOf { theme == IntUiThemes.Dark } }

                TitleBarView(
                    theme
                ) {
                    theme = when (theme) {
                        IntUiThemes.Light -> IntUiThemes.Dark
                        IntUiThemes.Dark, IntUiThemes.System -> IntUiThemes.Light
                    }
                }
                App(isDarkTheme = isDark, appColor = if (os.contains("Windows")) {
                    Color(paletteGeneration.getAccentColor())
                    } else null)
            }
        }
    }
}