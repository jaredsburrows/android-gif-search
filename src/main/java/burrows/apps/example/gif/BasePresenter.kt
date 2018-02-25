package burrows.apps.example.gif

interface BasePresenter<in T> {
  fun takeView(view: T)
  fun dropView()
}
