package presentation.mvi


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

@Composable
inline fun <reified STATE : MVIState, EVENT : MVIIntent, EFFECT : MVIAction> use(
    viewModel: BaseViewModel<EVENT, STATE, EFFECT>,
): StateDispatchEffect<STATE, EVENT, EFFECT> {
    val state by viewModel.uiState.collectAsState()

    val dispatch: (EVENT) -> Unit = { event ->
        viewModel.handleEvent(event)
    }

    return StateDispatchEffect(
        state = state,
        effectFlow = viewModel.effect.shareIn(
            viewModel.screenModelScope,
            SharingStarted.WhileSubscribed()
        ),
        dispatch = dispatch,
    )
}

data class StateDispatchEffect<STATE, EVENT, EFFECT>(
    val state: STATE,
    val dispatch: (EVENT) -> Unit,
    val effectFlow: SharedFlow<EFFECT>,
)