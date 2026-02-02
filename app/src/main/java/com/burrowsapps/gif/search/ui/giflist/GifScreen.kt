@file:OptIn(
  ExperimentalFoundationApi::class,
  ExperimentalMaterial3Api::class,
)

package com.burrowsapps.gif.search.ui.giflist

import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// Grid cell size for GIF thumbnails (3 columns fit on screen)
private val GifCellSize: Dp = 135.dp

// Full-size GIF dialog size
private val GifDialogSize: Dp = 350.dp

// Shared GIFs cache directory name
private const val SHARED_GIFS_DIR = "shared_gifs"

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
          val showEmptyState =
            pagingItems.itemCount == 0 &&
              pagingItems.loadState.refresh is LoadState.NotLoading &&
              pagingItems.loadState.append.endOfPaginationReached

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
            val gifImageContentDesc = stringResource(R.string.gif_image_content_description)
            Box(
              modifier =
                Modifier
                  .semantics {
                    contentDescription = gifImageContentDesc
                  }.clickable {
                    openDialog.value = true
                    currentSelectedItem.value = item
                  },
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
                    // Static placeholder to avoid per-cell animated spinners
                    Box(
                      modifier =
                        Modifier
                          .matchParentSize()
                          .background(MaterialTheme.colorScheme.surfaceVariant),
                    )
                  },
                )
              }
            }
          }
        }
      }
    }

    // No overlaid app bar; handled by Scaffold's topBar
  }
}

private suspend fun downloadGifFile(
  context: Context,
  gifUrl: String,
): File = withContext(Dispatchers.IO) {
  suspendCancellableCoroutine { continuation ->
    val futureTarget = Glide.with(context)
      .asFile()
      .load(gifUrl)
      .submit()

    continuation.invokeOnCancellation {
      futureTarget.cancel(true)
    }

    try {
      // Blocking call, but safe since we're already on Dispatchers.IO
      val file = futureTarget.get()
      continuation.resume(file)
    } catch (e: Exception) {
      continuation.resumeWithException(e)
    }
  }
}

private suspend fun downloadGif(
  context: Context,
  gifUrl: String,
  onSuccess: suspend () -> Unit,
  onError: suspend () -> Unit,
) {
  try {
    val file = downloadGifFile(context, gifUrl)

    withContext(Dispatchers.IO) {
      // Get the file name from URL
      val fileName = "gif_${System.currentTimeMillis()}.gif"

      // Save to Downloads folder using MediaStore
      val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/gif")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
      }

      val resolver = context.contentResolver
      val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

      uri?.let {
        resolver.openOutputStream(it)?.use { outputStream ->
          file.inputStream().use { inputStream ->
            inputStream.copyTo(outputStream)
          }
        }
        onSuccess()
      } ?: onError()
    }
  } catch (e: Exception) {
    onError()
  }
}

private suspend fun shareGif(
  context: Context,
  gifUrl: String,
  onError: suspend () -> Unit,
) {
  try {
    val file = downloadGifFile(context, gifUrl)

    val contentUri = withContext(Dispatchers.IO) {
      // Copy file to cache directory for stable sharing
      val cacheDir = File(context.cacheDir, SHARED_GIFS_DIR)
      cacheDir.mkdirs()
      
      // Clean up old cached files (older than 1 hour)
      cleanupOldCachedFiles(cacheDir)
      
      val cachedFile = File(cacheDir, "shared_${System.currentTimeMillis()}.gif")
      file.inputStream().use { input ->
        cachedFile.outputStream().use { output ->
          input.copyTo(output)
        }
      }

      // Create a content URI for the cached file
      androidx.core.content.FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        cachedFile,
      )
    }

    // Create and launch share intent on main thread
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
      type = "image/gif"
      putExtra(Intent.EXTRA_STREAM, contentUri)
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(shareIntent, null).apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
  } catch (e: Exception) {
    onError()
  }
}

private fun cleanupOldCachedFiles(cacheDir: File) {
  if (!cacheDir.exists()) return
  
  val oneHourAgo = System.currentTimeMillis() - (60 * 60 * 1000)
  cacheDir.listFiles()?.forEach { file ->
    if (file.lastModified() < oneHourAgo) {
      file.delete()
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
  val gifImageDialogContentDesc = stringResource(R.string.gif_image_dialog_content_description)
  val copiedToClipboardMsg = stringResource(R.string.copied_to_clipboard)
  val copyUrlText = stringResource(R.string.copy_url)
  val downloadImageText = stringResource(R.string.download_image)
  val shareImageText = stringResource(R.string.share_image)
  val imageSavedMsg = stringResource(R.string.image_saved)
  val downloadFailedMsg = stringResource(R.string.download_failed)
  val shareFailedMsg = stringResource(R.string.share_failed)

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
              contentDescription = gifImageDialogContentDesc
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

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
          TextButton(
            onClick = {
              onDialogDismiss(false)
              coroutineScope.launch {
                clipboardManager.setClipEntry(
                  ClipEntry(ClipData.newPlainText("gif url", currentSelectedItem.gifUrl)),
                )
                hostState.showSnackbar(
                  copiedToClipboardMsg,
                )
              }
            },
          ) {
            Text(
              text = copyUrlText,
              color = Color(palette.value?.lightMutedSwatch?.rgb ?: Color.White.toArgb()),
              fontSize = MaterialTheme.typography.titleMedium.fontSize,
            )
          }

          TextButton(
            onClick = {
              onDialogDismiss(false)
              coroutineScope.launch {
                downloadGif(
                  context = context,
                  gifUrl = currentSelectedItem.gifUrl,
                  onSuccess = {
                    hostState.showSnackbar(imageSavedMsg)
                  },
                  onError = {
                    hostState.showSnackbar(downloadFailedMsg)
                  },
                )
              }
            },
          ) {
            Text(
              text = downloadImageText,
              color = Color(palette.value?.lightMutedSwatch?.rgb ?: Color.White.toArgb()),
              fontSize = MaterialTheme.typography.titleMedium.fontSize,
            )
          }

          TextButton(
            onClick = {
              onDialogDismiss(false)
              coroutineScope.launch {
                shareGif(
                  context = context,
                  gifUrl = currentSelectedItem.gifUrl,
                  onError = {
                    hostState.showSnackbar(shareFailedMsg)
                  },
                )
              }
            },
          ) {
            Text(
              text = shareImageText,
              color = Color(palette.value?.lightMutedSwatch?.rgb ?: Color.White.toArgb()),
              fontSize = MaterialTheme.typography.titleMedium.fontSize,
            )
          }
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
  val request = Glide.with(context).asGif()
  val thumbnailRequest =
    request
      .load(thumbnailUrl)
      .override(size)
      .dontTransform()
      .signature(ObjectKey(thumbnailUrl))
  val imageRequest =
    request
      .load(imageUrl)
      .thumbnail(thumbnailRequest)
      .override(size)
      .dontTransform()
      .signature(ObjectKey(imageUrl))
  return imageRequest
}
