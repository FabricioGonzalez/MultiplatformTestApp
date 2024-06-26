package features.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import features.home.HomeScreen
import presentation.ui.common.AppBarState
import presentation.ui.common.AppScreen

class LoginScreen(
    override val route: String = "Login",
    override val onCompose: (AppBarState) -> Unit,
) : AppScreen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow


        val (user, setUser) = remember { mutableStateOf("") }
        val (password, setPassword) = remember { mutableStateOf("") }


        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            TextField(user, setUser)
            TextField(password, setPassword, visualTransformation = PasswordVisualTransformation())
            Button({
                if (user == "admin" && password == "library2022")

                    navigator.replace(HomeScreen(onCompose = onCompose))
            }) {
                Text("Login")
            }
        }
    }
}