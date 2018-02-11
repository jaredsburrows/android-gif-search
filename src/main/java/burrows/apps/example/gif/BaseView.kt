package burrows.apps.example.gif

interface BaseView<in T : BasePresenter> {
  fun setPresenter(presenter: T)
}
