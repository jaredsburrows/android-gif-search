@file:OptIn(
  ExperimentalFoundationApi::class,
  ExperimentalMaterial3Api::class,
  ExperimentalMaterialApi::class,
)

package com.burrowsapps.gif.search.ui.giflist

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Devices.PIXEL_7_PRO
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import com.burrowsapps.gif.search.R
import com.burrowsapps.gif.search.Screen
import com.burrowsapps.gif.search.ui.theme.GifTheme
import com.kmpalette.palette.graphics.Palette
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.glide.GlideRequestType.GIF
import com.skydoves.landscapist.glide.LocalGlideRequestBuilder
import com.skydoves.landscapist.palette.PalettePlugin
import kotlinx.coroutines.flow.distinctUntilChanged

/** Shows the main screen of trending gifs. */
@Preview(
  name = "dark",
  showBackground = true,
  device = PIXEL_7_PRO,
  locale = "en",
  showSystemUi = true,
  uiMode = UI_MODE_NIGHT_YES,
)
@Preview(
  name = "light",
  showBackground = true,
  device = PIXEL_7_PRO,
  locale = "en",
  showSystemUi = true,
  uiMode = UI_MODE_NIGHT_NO,
)
@Composable
private fun DefaultPreview(navController: NavHostController = rememberNavController()) {
  GifTheme {
    GifScreen(
      navController = navController,
    )
  }
}

@Composable
internal fun GifScreen(
  navController: NavHostController,
  modifier: Modifier = Modifier,
  gifViewModel: GifViewModel = hiltViewModel(),
) {
  val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

  LaunchedEffect(Unit) {
    gifViewModel.loadTrendingImages()
  }

  val listItems by gifViewModel.gifListResponse.collectAsState()
  val isRefreshing by gifViewModel.isRefreshing.collectAsState()
  val searchText by gifViewModel.searchText.collectAsState(initial = "")

  Scaffold(
    modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
      TheToolbar(
        navController = navController,
        scrollBehavior = scrollBehavior,
        searchText = searchText,
        onSearchTextChange = {
          gifViewModel.onSearchTextChanged(it)
          if (it.isNotEmpty()) {
            gifViewModel.loadSearchImages(it)
          } else {
            gifViewModel.loadTrendingImages()
          }
        },
        onClearClick = {
          gifViewModel.onClearClick()
          gifViewModel.loadTrendingImages()
        },
      )
    },
  ) { paddingValues ->
    TheContent(
      innerPadding = paddingValues,
      listItems = listItems,
      isRefreshing = isRefreshing,
      onRefresh = { gifViewModel.loadTrendingImages() },
      onLoadMore = { gifViewModel.loadMore() },
    )
  }
}

@Composable
private fun TheToolbar(
  navController: NavHostController,
  scrollBehavior: TopAppBarScrollBehavior,
  searchText: String,
  onSearchTextChange: (String) -> Unit,
  onClearClick: () -> Unit,
) {
  val openSearch = remember { mutableStateOf(true) }

  if (openSearch.value) {
    TheToolBar(
      navController = navController,
      scrollBehavior = scrollBehavior,
      openSearch = { openSearch.value = it },
    )
  } else {
    TheSearchBar(
      searchText = searchText,
      onSearchTextChange = onSearchTextChange,
      onClearClick = onClearClick,
      openSearch = { openSearch.value = it },
      scrollBehavior = scrollBehavior,
    )
  }
}

@Composable
private fun TheToolBar(
  navController: NavHostController,
  scrollBehavior: TopAppBarScrollBehavior,
  openSearch: (Boolean) -> Unit,
) {
  val showMenu = remember { mutableStateOf(false) }
  val searchTooltipState = remember { TooltipState() }
  val moreTooltipState = remember { TooltipState() }
  val context = LocalContext.current

  TopAppBar(
    title = {
      Text(
        text = stringResource(R.string.gif_screen_title),
      )
    },
    // Search Tooltip Box
    actions = {
      // Search for Gifs
      TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = { Text("Search gifs") },
        state = searchTooltipState,
      ) {
        IconButton(
          onClick = { openSearch(false) },
        ) {
          Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = stringResource(R.string.menu_search_content_description),
          )
        }
      }
      // More Tooltip Box
      TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = { Text("Show menu") },
        state = moreTooltipState,
      ) {
        IconButton(
          onClick = { showMenu.value = !showMenu.value },
        ) {
          Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = stringResource(R.string.menu_more_content_description),
          )
        }
      }
      // Overflow menu
      DropdownMenu(
        expanded = showMenu.value,
        onDismissRequest = { showMenu.value = false },
      ) {
        DropdownMenuItem(
          modifier =
            Modifier.semantics {
              contentDescription = context.getString(R.string.license_screen_content_description)
            },
          onClick = {
            showMenu.value = false
            navController.navigate(Screen.License.route)
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
  searchText: String,
  onSearchTextChange: (String) -> Unit,
  onClearClick: () -> Unit,
  openSearch: (Boolean) -> Unit,
  scrollBehavior: TopAppBarScrollBehavior,
) {
  BackHandler(
    onBack = {
      openSearch(true)
      onClearClick()
    },
  )

  SearchBar(
    scrollBehavior = scrollBehavior,
    searchText = searchText,
    placeholderText = stringResource(R.string.search_gifs),
    onSearchTextChange = {
      onSearchTextChange(it)
    },
    onClearClick = {
      onClearClick()
    },
  ) {
    openSearch(true)
    onClearClick()
  }
}

@Composable
private fun TheContent(
  innerPadding: PaddingValues,
  listItems: List<GifImageInfo>,
  isRefreshing: Boolean,
  onRefresh: () -> Unit,
  onLoadMore: () -> Unit,
) {
  Column(
    modifier = Modifier.padding(innerPadding),
  ) {
    val gridState = rememberLazyGridState()
    val openDialog = remember { mutableStateOf(false) }
    val currentSelectedItem = remember { mutableStateOf(GifImageInfo()) }

    if (openDialog.value) {
      TheDialogPreview(
        currentSelectedItem = currentSelectedItem.value,
        onDialogDismiss = { openDialog.value = it },
      )
    }
    val pullRefreshState =
      rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh,
      )

    Box(
      modifier =
        Modifier
          .pullRefresh(pullRefreshState),
      contentAlignment = Alignment.Center,
    ) {
      val context = LocalContext.current
      LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
      ) {
        // TODO update default state
        if (listItems.isEmpty()) {
          item {
            Text(
              text = "No Gifs!",
              style = MaterialTheme.typography.bodyLarge,
              modifier =
                Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 16.dp),
            )
          }
        }

        items(
          items = listItems,
        ) { item ->
          Box(
            modifier =
              Modifier
                .animateItemPlacement(
                  animationSpec = tween(durationMillis = 350),
                )
                .semantics {
                  contentDescription = context.getString(R.string.gif_image_content_description)
                },
          ) {
            val requestBuilder =
              loadGif(
                context = context,
                imageUrl = item.tinyGifUrl,
                thumbnailUrl = item.tinyGifPreviewUrl,
              )

            CompositionLocalProvider(LocalGlideRequestBuilder provides requestBuilder) {
              GlideImage(
                imageModel = { item.tinyGifUrl },
                glideRequestType = GIF,
                modifier =
                  Modifier
                    .padding(1.dp)
                    .fillMaxWidth()
                    .size(135.dp)
                    .clickable {
                      openDialog.value = true
                      currentSelectedItem.value = item
                    },
                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                loading = {
                  Box(modifier = Modifier.matchParentSize()) {
                    CircularProgressIndicator(
                      modifier = Modifier.align(Alignment.Center),
                    )
                  }
                },
              )
            }
          }
        }
      }

      PullRefreshIndicator(
        isRefreshing,
        pullRefreshState,
        modifier = Modifier.align(Alignment.TopCenter),
      )

      InfiniteGridHandler(
        gridState = gridState,
      ) {
        onLoadMore()
      }
    }
  }
}

@Composable
private fun TheDialogPreview(
  currentSelectedItem: GifImageInfo,
  onDialogDismiss: (Boolean) -> Unit,
) {
  val clipboardManager = LocalClipboardManager.current
  val context = LocalContext.current

  Dialog(
    onDismissRequest = {
      onDialogDismiss(false)
    },
  ) {
    Column(
      modifier =
        Modifier.semantics {
          contentDescription = context.getString(R.string.gif_image_dialog_content_description)
        },
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      val palette = remember { mutableStateOf<Palette?>(null) }
      val requestBuilder =
        loadGif(
          context = context,
          imageUrl = currentSelectedItem.gifUrl,
          thumbnailUrl = currentSelectedItem.gifPreviewUrl,
        )

      CompositionLocalProvider(LocalGlideRequestBuilder provides requestBuilder) {
        GlideImage(
          imageModel = { currentSelectedItem.gifUrl },
          glideRequestType = GIF,
          modifier =
            Modifier
              .padding(1.dp)
              .fillMaxWidth()
              .size(350.dp),
          component =
            rememberImageComponent {
              +PalettePlugin { palette.value = it }
            },
          imageOptions = ImageOptions(contentScale = ContentScale.Crop),
          loading = {
            Box(modifier = Modifier.matchParentSize()) {
              CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
              )
            }
          },
        )
      }

      TextButton(
        onClick = {
          onDialogDismiss(false)
          clipboardManager.setText(AnnotatedString(currentSelectedItem.gifUrl))
        },
      ) {
        Text(
          text = context.getString(R.string.copy_url),
          color = Color(palette.value?.lightMutedSwatch?.rgb ?: Color.White.toArgb()),
          fontSize = MaterialTheme.typography.titleMedium.fontSize,
        )
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
  val currentOnLoadMore by rememberUpdatedState(onLoadMore)
  val loadMore =
    remember {
      derivedStateOf {
        val layoutInfo = gridState.layoutInfo
        val totalItemsNumber = layoutInfo.totalItemsCount
        val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

        lastVisibleItemIndex > (totalItemsNumber - buffer)
      }
    }

  LaunchedEffect(loadMore) {
    snapshotFlow { loadMore.value }.distinctUntilChanged().collect {
      if (it) currentOnLoadMore()
    }
  }
}

private fun loadGif(
  context: Context,
  imageUrl: String,
  thumbnailUrl: String,
  size: Int = Target.SIZE_ORIGINAL,
): RequestBuilder<GifDrawable> {
  val request = Glide.with(context).asGif()
  val thumbnailRequest =
    request.transition(DrawableTransitionOptions.withCrossFade()).load(thumbnailUrl)
      .override(size).signature(ObjectKey(thumbnailUrl))
  val imageRequest =
    request.transition(DrawableTransitionOptions.withCrossFade()).load(imageUrl)
      .thumbnail(thumbnailRequest)
      .override(size).signature(ObjectKey(imageUrl))
  return imageRequest
}
