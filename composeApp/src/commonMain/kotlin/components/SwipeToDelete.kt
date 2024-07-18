package components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteBackground(
    modifier: Modifier = Modifier, swipeDismissState: SwipeToDismissBoxState
) {
    val color = when (swipeDismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> Color.Yellow
        SwipeToDismissBoxValue.EndToStart -> Color.Red
        SwipeToDismissBoxValue.Settled -> Color.Transparent
    }
    val icon = when (swipeDismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> Icons.Rounded.Edit
        SwipeToDismissBoxValue.EndToStart -> Icons.Rounded.Delete
        SwipeToDismissBoxValue.Settled -> null
    }
    Box(
        modifier = modifier.fillMaxSize().background(color).padding(16.dp), contentAlignment = Alignment.CenterEnd
    ) {
        icon?.let {
            Icon(
                modifier = Modifier.align(if (swipeDismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd),
                imageVector = it,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SwipeToDeleteContainer(
    modifier: Modifier = Modifier,
    item: T,
    onDelete: (T) -> Unit,
    onEdit: (T) -> Unit,
    animationDuration: Int = 500,
    content: @Composable (T) -> Unit
) {
    var isRemoved by remember {
        mutableStateOf(false)
    }
    var isEditing by remember {
        mutableStateOf(false)
    }
    val state = rememberSwipeToDismissBoxState(confirmValueChange = { value ->
        when (value) {
            SwipeToDismissBoxValue.StartToEnd -> {
                isEditing = true
                true
            }

            SwipeToDismissBoxValue.EndToStart -> {
                isRemoved = true
                true
            }

            else -> {
                false
            }
        }
    })
    LaunchedEffect(key1 = isRemoved) {
        if (isRemoved) {
            delay(animationDuration.toLong())
            onDelete(item)
        }
    }
    LaunchedEffect(key1 = isEditing) {
        if (isEditing) {
            delay(animationDuration.toLong())
            onEdit(item)
        }
    }

    AnimatedVisibility(
        visible = !isRemoved || !isEditing, exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration), shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismissBox(state = state, backgroundContent = {
            DeleteBackground(swipeDismissState = state, modifier = modifier.clip(MaterialTheme.shapes.medium))
        }, content = {
            content(item)
        })
    }
}