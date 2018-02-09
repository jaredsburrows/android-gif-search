package burrows.apps.example.gif.presentation

interface BaseView<in T : BasePresenter> {
  fun setPresenter(presenter: T)
}
