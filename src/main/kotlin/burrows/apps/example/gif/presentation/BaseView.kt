package burrows.apps.example.gif.presentation

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
interface BaseView<in T : BasePresenter> {
  fun setPresenter(presenter: T)
}
