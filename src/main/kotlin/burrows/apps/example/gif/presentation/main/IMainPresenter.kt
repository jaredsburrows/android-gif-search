package burrows.apps.example.gif.presentation.main

import burrows.apps.example.gif.presentation.IBasePresenter

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
interface IMainPresenter : IBasePresenter {
  fun clearImages()
  fun loadTrendingImages(next: Float)
  fun loadSearchImages(searchString: String, next: Float)
}
