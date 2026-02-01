@file:OptIn(
  ExperimentalFoundationApi::class,
  ExperimentalMaterial3Api::class,
)

package com.burrowsapps.gif.search.ui.giflist

import android.content.ClipData
import android.content.Context
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
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

// Grid cell size for GIF thumbnails (3 columns fit on screen)
private val GifCellSize: Dp = 135.dp

// Full-size GIF dialog size
private val GifDialogSize: Dp = 350.dp

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
      hostState = snackbarHostState,
    )
  }
}

@Composable
internal fun GifScreen(
  modifier: Modifier = Modifier,
  navController: NavHostController = rememberNavController(),
  gifViewModel: GifViewModel = hiltViewModel(),
  hostState: SnackbarHostState = remember { SnackbarHostState() },
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
    snackbarHost = { SnackbarHost(hostState) },
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
      hostState = hostState,
    )
  }
}

@Composable
private fun TheContent(
  innerPadding: PaddingValues,
  pagingItems: LazyPagingItems<GifImageInfo>,
  isRefreshing: Boolean,
  onRefresh: () -> Unit,
  hostState: SnackbarHostState,
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

    // Calculate cell size once, outside the items block
    val cellSizePx = with(LocalDensity.current) { GifCellSize.roundToPx() }

    // Use derivedStateOf to avoid recomposition when computed value doesn't change
    // even if individual properties are read multiple times
    val showEmptyState by remember(pagingItems.loadState) {
      derivedStateOf {
        pagingItems.itemCount == 0 &&
          pagingItems.loadState.refresh is LoadState.NotLoading &&
          pagingItems.loadState.append.endOfPaginationReached
      }
    }

    LaunchedEffect(gridState) {
      snapshotFlow { gridState.isScrollInProgress }.collect { isScrolling ->
        if (isScrolling) focusManager.clearFocus()
      }
    }
    // Main scrolling content
    Column(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(top = 8.dp),
    ) {
      val openDialog = remember { mutableStateOf(false) }
      val currentSelectedItem = remember { mutableStateOf(GifImageInfo()) }

      if (openDialog.value) {
        GifOverlay(
          currentSelectedItem = currentSelectedItem.value,
          onDialogDismiss = { openDialog.value = it },
          hostState = hostState,
        )
      }

      val pullToRefreshState = rememberPullToRefreshState()
      PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = pullToRefreshState,
        modifier =
          Modifier
            .weight(1f)
            .fillMaxWidth(),
      ) {
        LazyVerticalGrid(
          state = gridState,
          columns = GridCells.Fixed(3),
          modifier = Modifier.fillMaxSize(),
        ) {
          // No header item; collapse handled by scrollBehavior on the app bar
          // Empty state - only show if we're not loading and have no items
          if (showEmptyState) {
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
            key = pagingItems.itemKey { it.tinyGifUrl },
            contentType = pagingItems.itemContentType { "gif" },
          ) { index ->
            val item = pagingItems[index] ?: return@items

            GifGridItem(
              item = item,
              cellSizePx = cellSizePx,
              onItemClick = {
                openDialog.value = true
                currentSelectedItem.value = item
              },
            )
          }
        }
      }
    }

    // No overlaid app bar; handled by Scaffold's topBar
  }
}

/**
 * Grid item composable for displaying a GIF thumbnail.
 * 
 * Extracted from the items{} block to reduce complexity and improve scroll performance.
 * Uses remember for Glide requests and drawWithCache for static placeholders to minimize
 * recompositions during scrolling.
 * 
 * @param item The GIF image information to display
 * @param cellSizePx The cell size in pixels for the thumbnail
 * @param onItemClick Callback when the item is clicked
 */
@Composable
private fun GifGridItem(
  item: GifImageInfo,
  cellSizePx: Int,
  onItemClick: () -> Unit,
) {
  val context = LocalContext.current
  val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
  
  Box(
    modifier =
      Modifier
        .semantics {
          contentDescription = context.getString(R.string.gif_image_content_description)
        }.clickable(onClick = onItemClick),
  ) {
    // Remember Glide request, invalidate on URL, context, or size changes
    // Context changes on configuration change, cellSizePx changes on density change
    val requestBuilder =
      remember(item.tinyGifUrl, item.tinyGifPreviewUrl, context, cellSizePx) {
        loadGif(
          context = context,
          imageUrl = item.tinyGifUrl,
          thumbnailUrl = item.tinyGifPreviewUrl,
          size = cellSizePx,
        )
      }

    CompositionLocalProvider(LocalGlideRequestBuilder provides requestBuilder) {
      GlideImage(
        imageModel = { item.tinyGifUrl },
        glideRequestType = GIF,
        modifier =
          Modifier
            .padding(1.dp)
            .fillMaxWidth()
            .size(GifCellSize),
        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
        loading = {
          // Use drawWithCache for better performance - caches the draw operation
          // surfaceColor captures theme color; cache invalidates on theme change (as intended)
          Box(
            modifier =
              Modifier
                .matchParentSize()
                .drawWithCache {
                  onDrawBehind {
                    drawRect(color = surfaceColor)
                  }
                },
          )
        },
      )
    }
  }
}

@Composable
private fun GifOverlay(
  currentSelectedItem: GifImageInfo?,
  onDialogDismiss: (Boolean) -> Unit,
  hostState: SnackbarHostState,
) {
  if (currentSelectedItem == null) return

  val clipboardManager = LocalClipboard.current
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val palette = remember { mutableStateOf<Palette?>(null) }

  // Root scrim + modal layout
  Box(
    modifier =
      Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.6f))
        .clickable(onClick = { onDialogDismiss(false) }),
    contentAlignment = Alignment.Center,
  ) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      tonalElevation = 8.dp,
      color = MaterialTheme.colorScheme.surface,
      modifier =
        Modifier
          .padding(24.dp)
          .wrapContentHeight()
          .fillMaxWidth(),
    ) {
      Column(
        modifier =
          Modifier
            .padding(16.dp)
            .semantics {
              contentDescription = context.getString(R.string.gif_image_dialog_content_description)
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
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
                .size(GifDialogSize),
            component =
              rememberImageComponent {
                +PalettePlugin { palette.value = it }
              },
            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
            loading = {
              Box(modifier = Modifier.matchParentSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
              hostState.showSnackbar(
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

  // Optional back button handling
  BackHandler {
    onDialogDismiss(false)
  }
}

private fun loadGif(
  context: Context,
  imageUrl: String,
  thumbnailUrl: String,
  size: Int = Target.SIZE_ORIGINAL,
): RequestBuilder<GifDrawable> {
  // Create thumbnail request builder
  val thumbnailRequest =
    Glide.with(context)
      .asGif()
      .load(thumbnailUrl)
      .override(size)
      .dontTransform()
      .signature(ObjectKey(thumbnailUrl))

  // Create and return the main image request with thumbnail
  return Glide.with(context)
    .asGif()
    .load(imageUrl)
    .thumbnail(thumbnailRequest)
    .override(size)
    .dontTransform()
    .signature(ObjectKey(imageUrl))
}
