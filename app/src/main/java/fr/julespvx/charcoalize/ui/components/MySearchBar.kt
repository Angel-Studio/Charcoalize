package fr.julespvx.charcoalize.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Preview
@Composable
fun MySearchBar(
    modifier: Modifier = Modifier,
    state: MutableState<TextFieldValue> =
        remember { mutableStateOf(TextFieldValue("")) },
    onValueChange: (String) -> Unit = { },
    placeholder: String = "Search",
    results: @Composable () -> Unit = { },
    showResults: Boolean = false,
    requestFocus: Boolean = false,
) {
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(animateFloatAsState(targetValue = if (!showResults) 100f else 35f).value))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .animateContentSize(),
    ) {
        TextField(
            value = state.value,
            onValueChange = { value ->
                onValueChange(value.text)
                state.value = value
            },
            modifier = modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            leadingIcon = {
                Icon(
                    Icons.Rounded.Search,
                    contentDescription = "",
                )
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = state.value != TextFieldValue(""),
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                ) {
                    IconButton(
                        onClick = {
                            state.value = TextFieldValue("")
                            onValueChange("")
                        },
                    ) {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = "",
                        )
                    }
                }
            },
            singleLine = true,
            shape = CircleShape,
            colors = TextFieldDefaults.textFieldColors(
                cursorColor = MaterialTheme.colorScheme.onSurface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                containerColor = Color.Transparent,
            ),
            placeholder = { Text(placeholder) },
        )
        AnimatedVisibility(
            visible = showResults,
            enter = slideInVertically() + expandVertically() + fadeIn(),
            exit = slideOutVertically() + shrinkVertically() + fadeOut(),
        ) {
            results()
        }

        LaunchedEffect(Unit) {
            if (requestFocus) {
                focusRequester.requestFocus()
            }
        }
    }
}