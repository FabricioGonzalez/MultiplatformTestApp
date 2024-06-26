import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ModeNight
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Mode
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Tooltip
import org.jetbrains.jewel.window.DecoratedWindowScope
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DecoratedWindowScope.TitleBarView(theme: IntUiThemes, changeTheme: () -> Unit) {
    TitleBar(Modifier.newFullscreenControls()) {
        Text(title)

        Row(Modifier.align(Alignment.End)) {
            Tooltip({
                when (theme) {
                    IntUiThemes.Light -> Text("Switch to light theme with light header")
                    IntUiThemes.Dark, IntUiThemes.System -> Text("Switch to light theme")
                }
            }) {
                IconButton(onClick = { changeTheme() }, Modifier.size(40.dp).padding(5.dp)) {
                    when (theme) {
                        IntUiThemes.Light -> Icon(
                            imageVector = Icons.Rounded.LightMode,
                            tint = Color.White,
                            contentDescription = "Themes",
                        )

                        IntUiThemes.Dark -> Icon(
                            imageVector = Icons.Outlined.ModeNight,
                            tint = Color.White,
                            contentDescription = "Themes",
                        )

                        IntUiThemes.System -> Icon(
                            imageVector = Icons.Rounded.Mode,
                            tint = Color.White,
                            contentDescription = "Themes",
                        )
                    }
                }
            }
        }
    }
}