@file:OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalFoundationApi::class,
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
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import kotlin.math.roundToInt

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
    GifScreen(navController)
  }
}

@Composable
internal fun GifScreen(navController: NavHostController) {
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
    },
  ) { paddingValues ->
    val listItems = gifViewModel.gifListResponse.collectAsState()
    val isRefreshing = gifViewModel.isRefreshing.collectAsState()

    TheContent(
      innerPadding = paddingValues,
      gifViewModel = gifViewModel,
      listItems = listItems,
      isRefreshing = isRefreshing,
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
  val searchTooltipState = remember { TooltipState() }
  val moreTooltipState = remember { TooltipState() }
  val context = LocalContext.current

  TopAppBar(
    title = {
      Text(
        text = stringResource(R.string.gif_screen_title),
      )
    },
    // Search for Gifs
    actions = {
      // Search for Gifs
      TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = { Text("Search gifs") },
        state = searchTooltipState,
      ) {
        IconButton(
          onClick = { openSearch.value = false },
        ) {
          Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = stringResource(R.string.menu_search_content_description),
          )
        }
      }
      // Overflow menu item
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

  val searchText = gifViewModel.searchText.collectAsState(initial = "")

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
  ) {
    openSearch.value = true
    gifViewModel.onClearClick()
    gifViewModel.loadTrendingImages()
  }
}

@Composable
private fun TheContent(
  innerPadding: PaddingValues,
  gifViewModel: GifViewModel,
  listItems: State<List<GifImageInfo>>,
  isRefreshing: State<Boolean>,
) {
  Column(
    modifier = Modifier.padding(innerPadding),
  ) {
    val gridState = rememberLazyGridState()
    val openDialog = remember { mutableStateOf(false) }
    val currentSelectedItem = remember { mutableStateOf(GifImageInfo()) }

    if (openDialog.value) {
      TheDialogPreview(
        currentSelectedItem = currentSelectedItem,
        openDialog = openDialog,
      )
    }
    val pullRefreshState =
      rememberPullRefreshState(
        refreshing = isRefreshing.value,
        onRefresh = {
          gifViewModel.loadTrendingImages()
        },
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
        if (listItems.value.isEmpty()) {
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
          items = listItems.value,
          // TODO update key
//          key = { item -> item.gifUrl },
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
                size = 135.dp.value.roundToInt(),
              )

            CompositionLocalProvider(LocalGlideRequestBuilder provides requestBuilder) {
              GlideImage(
                imageModel = { item.tinyGifUrl },
                glideRequestType = GIF,
                modifier =
                  Modifier
                    .padding(1.dp)
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
        isRefreshing.value,
        pullRefreshState,
        modifier = Modifier.align(Alignment.TopCenter),
      )

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
  currentSelectedItem: MutableState<GifImageInfo>,
  openDialog: MutableState<Boolean>,
) {
  val clipboardManager = LocalClipboardManager.current
  val context = LocalContext.current

  Dialog(
    onDismissRequest = {
      openDialog.value = false
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
          imageUrl = currentSelectedItem.value.gifUrl,
          thumbnailUrl = currentSelectedItem.value.gifPreviewUrl,
        )

      CompositionLocalProvider(LocalGlideRequestBuilder provides requestBuilder) {
        GlideImage(
          imageModel = { currentSelectedItem.value.gifUrl },
          glideRequestType = GIF,
          modifier =
            Modifier
              .padding(1.dp)
              .fillMaxWidth()
              .height(350.dp),
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
          openDialog.value = false
          clipboardManager.setText(AnnotatedString(currentSelectedItem.value.gifUrl))
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
      onLoadMore()
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
