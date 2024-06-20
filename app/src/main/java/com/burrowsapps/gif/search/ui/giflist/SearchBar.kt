@file:OptIn(ExperimentalMaterial3Api::class)

package com.burrowsapps.gif.search.ui.giflist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.burrowsapps.gif.search.R

// Based off of https://github.com/vima9/compose-search-view-sample/blob/master/app/src/main/java/com/dbab/composesearchviewsample/components/SearchBarUI.kt
@Composable
internal fun SearchBar(
  scrollBehavior: TopAppBarScrollBehavior,
  searchText: String,
  placeholderText: String = "",
  onSearchTextChange: (String) -> Unit = {},
  onClearClick: () -> Unit = {},
  onNavigateBack: () -> Unit = {},
) {
  val keyboardController = LocalSoftwareKeyboardController.current
  val showClearButton = remember { mutableStateOf(false) }
  val focusRequester = remember { FocusRequester() }

  TopAppBar(
    title = { Text(text = "") },
    navigationIcon = {
      IconButton(onClick = { onNavigateBack() }) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = stringResource(id = R.string.menu_back_content_description),
        )
      }
    },
    actions = {
      OutlinedTextField(
        modifier =
          Modifier
            .fillMaxWidth(0.90F)
            .padding(2.dp)
            .onFocusChanged { focusState ->
              showClearButton.value = focusState.isFocused
            }
            .focusRequester(focusRequester),
        value = searchText,
        onValueChange = onSearchTextChange,
        placeholder = {
          Text(text = placeholderText)
        },
        colors =
          OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
          ),
        trailingIcon = {
          AnimatedVisibility(
            visible = showClearButton.value,
            enter = fadeIn(),
            exit = fadeOut(),
          ) {
            IconButton(onClick = { onClearClick() }) {
              Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(R.string.menu_close_content_description),
              )
            }
          }
        },
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions =
          KeyboardActions(
            onDone = {
              keyboardController?.hide()
            },
          ),
      )
    },
    scrollBehavior = scrollBehavior,
  )

  LaunchedEffect(Unit) {
    focusRequester.requestFocus()
  }
}
