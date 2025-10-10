@file:OptIn(ExperimentalMaterial3Api::class)

package com.burrowsapps.gif.search.ui.giflist

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarScrollBehavior
import androidx.compose.material3.Text
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.burrowsapps.gif.search.R

@Composable
internal fun KeepStyleTopBar(
  query: String,
  onQueryChange: (String) -> Unit,
  scrollBehavior: SearchBarScrollBehavior,
  onFocusChange: (Boolean) -> Unit,
  onOpenLicenses: () -> Unit,
) {
  val showMenu = remember { mutableStateOf(false) }

  val state = rememberSearchBarState()
  val interactionSource = remember { MutableInteractionSource() }
  val isFocused = interactionSource.collectIsFocusedAsState().value
  val latestOnFocusChange by rememberUpdatedState(onFocusChange)
  LaunchedEffect(isFocused) { latestOnFocusChange(isFocused) }

  TopSearchBar(
    state = state,
    scrollBehavior = scrollBehavior,
    colors = SearchBarDefaults.colors(),
    tonalElevation = SearchBarDefaults.TonalElevation,
    shadowElevation = SearchBarDefaults.ShadowElevation,
    modifier = Modifier.fillMaxWidth(),
    inputField = {
      SearchBarDefaults.InputField(
        modifier = Modifier.fillMaxWidth(),
        query = query,
        onQueryChange = onQueryChange,
        onSearch = { /* filtering happens as you type */ },
        expanded = false,
        onExpandedChange = { /* keep collapsed */ },
        placeholder = { Text(text = stringResource(R.string.search_hint)) },
        interactionSource = interactionSource,
        leadingIcon = {
          Icon(
            painter = painterResource(id = R.drawable.search_24),
            contentDescription = stringResource(R.string.menu_search_content_description),
          )
        },
        trailingIcon = {
          if (query.isNotEmpty()) {
            IconButton(onClick = { onQueryChange("") }) {
              Icon(
                painter = painterResource(id = R.drawable.close_24),
                contentDescription = stringResource(R.string.menu_close_content_description),
              )
            }
          } else {
            IconButton(onClick = { showMenu.value = true }) {
              Icon(
                painter = painterResource(id = R.drawable.more_vert_24),
                contentDescription = stringResource(R.string.menu_more_content_description),
              )
            }
            DropdownMenu(expanded = showMenu.value, onDismissRequest = { showMenu.value = false }) {
              DropdownMenuItem(
                text = { Text(text = stringResource(R.string.license_screen_title)) },
                onClick = {
                  showMenu.value = false
                  onOpenLicenses()
                },
              )
            }
          }
        },
        colors = SearchBarDefaults.inputFieldColors(),
      )
    },
  )
}
