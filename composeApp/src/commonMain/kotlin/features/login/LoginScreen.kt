package features.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import features.navigation.navigateToHome
import presentation.ui.common.AppBarState
import presentation.ui.common.AppScreen

class LoginScreen(
    override val route: String = "Login",
    override val onCompose: (AppBarState) -> Unit,
) : AppScreen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val focusManager = LocalFocusManager.current
        val (user, setUser) = remember { mutableStateOf("") }
        val (password, setPassword) = remember { mutableStateOf("") }
        val (passwordVisibility, setpasswordVisibility) = remember { mutableStateOf(false) }

        val loginFunction = {
            if (user == "admin" && password == "library2022") navigator.navigateToHome(onCompose = onCompose)
        }

        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            TextField(
                user, setUser, singleLine = true, keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Next)
                }), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            TextField(password,
                setPassword,
                singleLine = true,
                visualTransformation = if (!passwordVisibility) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardActions = KeyboardActions(onDone = {
                    loginFunction()
                }),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Password
                ),
                trailingIcon = {
                    IconButton({ setpasswordVisibility(!passwordVisibility) }) {
                        Icon(
                            if (!passwordVisibility) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                            null
                        )
                    }
                })
            Button(onClick = loginFunction) {
                Text("Login")
            }
        }
    }
}