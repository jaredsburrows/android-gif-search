package burrows.apps.example.gif.presentation

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
interface IBaseView<in T> {
  fun setPresenter(presenter: T)
}
