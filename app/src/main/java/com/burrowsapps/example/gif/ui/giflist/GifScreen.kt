@file:OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalFoundationApi::class,
  ExperimentalMaterial3Api::class,
)

package com.burrowsapps.example.gif.ui.giflist

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Devices.PIXEL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.burrowsapps.example.gif.BuildConfig
import com.burrowsapps.example.gif.R
import com.burrowsapps.example.gif.Screen
import com.burrowsapps.example.gif.data.ImageService
import com.burrowsapps.example.gif.ui.theme.GifTheme
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.flow.distinctUntilChanged

/** Shows the main screen of trending gifs. */
@Preview(
  name = "dark",
  showBackground = true,
  device = PIXEL,
  locale = "en",
  showSystemUi = true,
  uiMode = UI_MODE_NIGHT_YES,
)
@Preview(
  name = "light",
  showBackground = true,
  device = PIXEL,
  locale = "en",
  showSystemUi = true,
  uiMode = UI_MODE_NIGHT_NO,
)
@Composable
private fun DefaultPreview(
  navController: NavHostController = rememberNavController(),
  imageService: ImageService = ImageService(LocalContext.current)
) {
  GifTheme {
    GifScreen(navController, imageService)
  }
}

@Composable
internal fun GifScreen(
  navController: NavHostController,
  imageService: ImageService,
) {
  val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
  val gifViewModel = hiltViewModel<GifViewModel>()

  Scaffold(
    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
      TheToolbar(
        navController = navController,
        scrollBehavior = scrollBehavior,
        gifViewModel = gifViewModel,
      )
    }
  ) { paddingValues ->
    TheContent(
      innerPadding = paddingValues,
      imageService = imageService,
      gifViewModel = gifViewModel,
    )
  }
}

@Composable
private fun TheToolbar(
  navController: NavHostController,
  scrollBehavior: TopAppBarScrollBehavior,
  gifViewModel: GifViewModel,
) {
  val openSearch = remember { mutableStateOf(true) }
  val showMenu = remember { mutableStateOf(false) }

  if (openSearch.value) {
    TheToolBar(
      navController = navController,
      scrollBehavior = scrollBehavior,
      openSearch = openSearch,
      showMenu = showMenu,
    )
  } else {
    TheSearchBar(
      gifViewModel = gifViewModel,
      openSearch = openSearch,
      scrollBehavior = scrollBehavior,
    )
  }
}

@Composable
private fun TheToolBar(
  navController: NavHostController,
  scrollBehavior: TopAppBarScrollBehavior,
  openSearch: MutableState<Boolean>,
  showMenu: MutableState<Boolean>,
) {
  TopAppBar(
    title = {
      Text(
        text = stringResource(R.string.gif_screen_title),
      )
    },
    // Search for Gifs
    actions = {
      IconButton(
        onClick = { openSearch.value = false },
      ) {
        Icon(
          imageVector = Icons.Filled.Search,
          contentDescription = stringResource(R.string.menu_search),
        )
      }
      // Overflow menu item
      IconButton(
        onClick = { showMenu.value = !showMenu.value },
      ) {
        Icon(
          imageVector = Icons.Filled.MoreVert,
          contentDescription = stringResource(R.string.menu_more),
        )
      }
      // Overflow menu
      DropdownMenu(
        expanded = showMenu.value,
        onDismissRequest = { showMenu.value = false },
      ) {
        DropdownMenuItem(
          onClick = {
            navController.navigate(Screen.LicenseScreen.route)
            showMenu.value = false
          },
          text = { Text(text = stringResource(R.string.license_screen_title)) },
        )
      }
    },
    scrollBehavior = scrollBehavior,
  )
}

@Composable
private fun TheSearchBar(
  gifViewModel: GifViewModel,
  openSearch: MutableState<Boolean>,
  scrollBehavior: TopAppBarScrollBehavior,
) {
  BackHandler(
    onBack = {
      openSearch.value = true
      gifViewModel.onClearClick()
      gifViewModel.loadTrendingImages()
    },
  )

  val searchText by gifViewModel.searchText.collectAsState(initial = "")

  SearchBar(
    scrollBehavior = scrollBehavior,
    searchText = searchText,
    placeholderText = stringResource(R.string.search_gifs),
    onSearchTextChanged = {
      gifViewModel.onSearchTextChanged(changedSearchText = it)
      gifViewModel.loadSearchImages(searchString = it)
    },
    onClearClick = {
      gifViewModel.onClearClick()
      gifViewModel.loadTrendingImages()
    },
    onNavigateBack = {
      openSearch.value = true
      gifViewModel.onClearClick()
      gifViewModel.loadTrendingImages()
    },
  )
}

@Composable
private fun TheContent(
  innerPadding: PaddingValues,
  imageService: ImageService,
  gifViewModel: GifViewModel,
) {
  Column(
    modifier = Modifier
      .padding(innerPadding)
  ) {
    val gridState = rememberLazyGridState()
    val listItems by gifViewModel.gifListResponse.collectAsState()
    val isRefreshing by gifViewModel.isRefreshing.collectAsState()
    val openDialog = remember { mutableStateOf(false) }
    val currentSelectedItem = remember { mutableStateOf(GifImageInfo()) }
    val showGridProgressBar = remember { mutableStateOf(true) }
    val showPreviewProgressBar = remember { mutableStateOf(true) }
    val imageState = remember { mutableStateOf<GifDrawable?>(null) }
    val imagePreviewState = remember { mutableStateOf<GifDrawable?>(null) }

    if (openDialog.value) {
      TheDialogPreview(
        imageService = imageService,
        currentSelectedItem = currentSelectedItem,
        showPreviewProgressBar = showPreviewProgressBar,
        imagePreviewState = imagePreviewState,
        openDialog = openDialog,
      )
    }

    SwipeRefresh(
      state = rememberSwipeRefreshState(isRefreshing),
      onRefresh = {
        // TODO handle trending vs search
        gifViewModel.loadTrendingImages()
      },
    ) {
      LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(3),
        modifier = Modifier
          .fillMaxSize()
          .semantics { contentDescription = "Gif List" },
      ) {

        // TODO update default state
        if (listItems.isEmpty()) {
          item {
            Text(
              text = "No Gifs!",
              style = MaterialTheme.typography.bodyLarge,
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
            )
          }
        }

        items(
          items = listItems,
          // TODO remove for testing
          key = if (BuildConfig.BASE_URL.contains("tenor")) {
            null
          } else { item ->
            item.tinyGifUrl
          },
        ) { item ->
          BoxWithConstraints(
            modifier = Modifier
              .animateItemPlacement(
                animationSpec = tween(durationMillis = 350)
              )
          ) {
            imageService.loadGif(
              imageUrl = item.tinyGifUrl,
              thumbnailUrl = item.tinyGifPreviewUrl,
              onResourceReady = { resource ->
                showGridProgressBar.value = false
                imageState.value = resource
              },
              onLoadFailed = {
                showGridProgressBar.value = false
                imageState.value = null
              },
            )

            if (showGridProgressBar.value) {
              CircularProgressIndicator(
                modifier = Modifier
                  .width(75.dp)
                  .height(75.dp)
                  .padding(24.dp),
              )
            } else {
              Image(
                painter = rememberDrawablePainter(imageState.value),
                contentDescription = stringResource(R.string.gif_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                  .padding(1.dp)
                  .width(135.dp)
                  .height(135.dp)
                  .clickable {
                    currentSelectedItem.value = item
                    openDialog.value = true
                  },
              )
            }
          }
        }
      }

      InfiniteGridHandler(
        gridState = gridState,
      ) {
        gifViewModel.loadMore()
      }
    }
  }
}

@Composable
private fun TheDialogPreview(
  imageService: ImageService,
  currentSelectedItem: MutableState<GifImageInfo>,
  showPreviewProgressBar: MutableState<Boolean>,
  imagePreviewState: MutableState<GifDrawable?>,
  openDialog: MutableState<Boolean>,
) {
  val clipboardManager = LocalClipboardManager.current

  Dialog(
    onDismissRequest = {
      openDialog.value = false
    }
  ) {
    Column(
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      imageService.loadGif(
        imageUrl = currentSelectedItem.value.tinyGifUrl, // TODO Use gifUrl
        onResourceReady = { resource ->
          showPreviewProgressBar.value = false
          imagePreviewState.value = resource
        },
        onLoadFailed = {
          showPreviewProgressBar.value = false
          imagePreviewState.value = null
        },
      )

      // Show loading indicator when image is not loaded
      if (showPreviewProgressBar.value) {
        CircularProgressIndicator(
          modifier = Modifier
            .height(75.dp)
            .padding(24.dp),
        )
      } else {
        Image(
          painter = rememberDrawablePainter(imagePreviewState.value),
          contentDescription = stringResource(R.string.gif_image),
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        )
        TextButton(
          onClick = {
            openDialog.value = false
            clipboardManager.setText(AnnotatedString(currentSelectedItem.value.gifUrl))
          }
        ) {
          Text(text = "Copy URL")
        }
      }
    }
  }
}

@Composable
private fun InfiniteGridHandler(
  gridState: LazyGridState,
  buffer: Int = 15,
  onLoadMore: () -> Unit,
) {
  val loadMore = remember {
    derivedStateOf {
      val layoutInfo = gridState.layoutInfo
      val totalItemsNumber = layoutInfo.totalItemsCount
      val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

      lastVisibleItemIndex > (totalItemsNumber - buffer)
    }
  }

  LaunchedEffect(loadMore) {
    snapshotFlow { loadMore.value }
      .distinctUntilChanged()
      .collect {
        onLoadMore()
      }
  }
}
