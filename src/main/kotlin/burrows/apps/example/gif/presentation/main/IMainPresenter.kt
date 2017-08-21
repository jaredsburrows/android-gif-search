package burrows.apps.example.gif.presentation.main

import burrows.apps.example.gif.presentation.IBasePresenter

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
interface IMainPresenter : IBasePresenter {
  fun clearImages()
  fun loadTrendingImages(next: Int)
  fun loadSearchImages(searchString: String, next: Int)
}
