@file:OptIn(
  ExperimentalFoundationApi::class,
  ExperimentalMaterial3Api::class,
  ExperimentalMaterialApi::class,
)

package com.burrowsapps.gif.search.ui.giflist

import android.content.ClipData
import android.content.Context
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
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
import kotlinx.coroutines.launch

/** Shows the main screen of trending gifs. */
@Preview(
  name = "dark",
  locale = "en",
  showSystemUi = true,
  showBackground = true,
  uiMode = Configuration.UI_MODE_NIGHT_YES,
  device = Devices.PIXEL_7_PRO,
)
@Preview(
  name = "light",
  locale = "en",
  showSystemUi = true,
  showBackground = true,
  uiMode = Configuration.UI_MODE_NIGHT_NO,
  device = Devices.PIXEL_7_PRO,
)
@Composable
private fun DefaultPreview(navController: NavHostController = rememberNavController()) {
  GifTheme {
    val snackbarHostState = remember { SnackbarHostState() }
    GifScreen(
      navController = navController,
      snackbarHostState = snackbarHostState,
    )
  }
}

@Composable
internal fun GifScreen(
  modifier: Modifier = Modifier,
  navController: NavHostController = rememberNavController(),
  gifViewModel: GifViewModel = hiltViewModel(),
  snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
  val pagingItems = gifViewModel.gifPagingData.collectAsLazyPagingItems()
  val isRefreshing = pagingItems.loadState.refresh is LoadState.Loading
  val searchText by gifViewModel.searchText.collectAsState(initial = "")

  var isSearchFocused by remember { mutableStateOf(false) }
  val scrollBehavior =
    SearchBarDefaults.enterAlwaysSearchBarScrollBehavior(
      canScroll = { !isSearchFocused },
    )

  val focusManager = LocalFocusManager.current
  BackHandler(enabled = isSearchFocused) { focusManager.clearFocus() }
  Scaffold(
    modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      KeepStyleTopBar(
        query = searchText,
        onQueryChange = { gifViewModel.onSearchTextChanged(it) },
        scrollBehavior = scrollBehavior,
        onFocusChange = { isSearchFocused = it },
        onOpenLicenses = { navController.navigate(Screen.License.route) },
      )
    },
    // Use default insets so content and IME behave correctly
  ) { paddingValues ->
    TheContent(
      innerPadding = paddingValues,
      pagingItems = pagingItems,
      isRefreshing = isRefreshing,
      onRefresh = { pagingItems.refresh() },
      snackbarHostState = snackbarHostState,
    )
  }
}

// Old toolbar/search bar removed; search is integrated into content

@Composable
private fun TheContent(
  innerPadding: PaddingValues,
  pagingItems: LazyPagingItems<GifImageInfo>,
  isRefreshing: Boolean,
  onRefresh: () -> Unit,
  snackbarHostState: SnackbarHostState,
) {
  Box(
    modifier =
      Modifier
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
        .fillMaxSize()
        .imePadding(),
  ) {
    val context = LocalContext.current
    val gridState = rememberLazyGridState()
    val focusManager = LocalFocusManager.current
    LaunchedEffect(gridState) {
      snapshotFlow { gridState.isScrollInProgress }.collect { isScrolling ->
        if (isScrolling) focusManager.clearFocus()
      }
    }
    // Main scrolling content
    Column(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
      val openDialog = remember { mutableStateOf(false) }
      val currentSelectedItem = remember { mutableStateOf(GifImageInfo()) }

      if (openDialog.value) {
        TheDialogPreview(
          currentSelectedItem = currentSelectedItem.value,
          onDialogDismiss = { openDialog.value = it },
          snackbarHostState = snackbarHostState,
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
            .weight(1f)
            .fillMaxWidth()
            .pullRefresh(pullRefreshState),
        contentAlignment = Alignment.Center,
      ) {
        LazyVerticalGrid(
          state = gridState,
          columns = GridCells.Fixed(3),
          modifier = Modifier.fillMaxSize(),
        ) {
          // No header item; collapse handled by scrollBehavior on the app bar
          // Empty state
          if (pagingItems.itemCount == 0 && pagingItems.loadState.refresh is LoadState.NotLoading) {
            item {
              Text(
                text = stringResource(R.string.no_gifs),
                style = MaterialTheme.typography.bodyLarge,
                modifier =
                  Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
              )
            }
          }

          items(
            count = pagingItems.itemCount,
          ) { index ->
            val item = pagingItems[index] ?: return@items
            Box(
              modifier =
                Modifier
                  .animateItem(
                    fadeInSpec = null,
                    fadeOutSpec = null,
                    placementSpec = tween(durationMillis = 350),
                  ).semantics {
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
      }
    }

    // No overlaid app bar; handled by Scaffold's topBar
  }
}

@Composable
private fun TheDialogPreview(
  currentSelectedItem: GifImageInfo,
  onDialogDismiss: (Boolean) -> Unit,
  snackbarHostState: SnackbarHostState,
) {
  val clipboardManager = LocalClipboard.current
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

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
          coroutineScope.launch {
            clipboardManager.setClipEntry(
              ClipEntry(ClipData.newPlainText("gif url", currentSelectedItem.gifUrl)),
            )
            snackbarHostState.showSnackbar(
              context.getString(R.string.copied_to_clipboard),
            )
          }
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

private fun loadGif(
  context: Context,
  imageUrl: String,
  thumbnailUrl: String,
  size: Int = Target.SIZE_ORIGINAL,
): RequestBuilder<GifDrawable> {
  val request = Glide.with(context).asGif()
  val thumbnailRequest =
    request
      .transition(DrawableTransitionOptions.withCrossFade())
      .load(thumbnailUrl)
      .override(size)
      .signature(ObjectKey(thumbnailUrl))
  val imageRequest =
    request
      .transition(DrawableTransitionOptions.withCrossFade())
      .load(imageUrl)
      .thumbnail(thumbnailRequest)
      .override(size)
      .signature(ObjectKey(imageUrl))
  return imageRequest
}
