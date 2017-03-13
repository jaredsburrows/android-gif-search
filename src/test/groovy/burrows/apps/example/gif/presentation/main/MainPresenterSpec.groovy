package burrows.apps.example.gif.presentation.main

import burrows.apps.example.gif.data.rest.model.RiffsyResponse
import burrows.apps.example.gif.data.rest.repository.RiffsyRepository
import burrows.apps.example.gif.presentation.IBaseSchedulerProvider
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import test.BaseSpec

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
final class MainPresenterSpec extends BaseSpec {
  IMainView view = Mock()
  IBaseSchedulerProvider scheduler = Mock()
  RiffsyRepository repository = Mock()
  MainPresenter sut

  def "setup"() {
    scheduler.io() >> Schedulers.trampoline()
    scheduler.ui() >> Schedulers.trampoline()

    view.isActive() >> true
  }

  def "load trending images not active"() {
    given:
    RiffsyResponse response = new RiffsyResponse()
    sut = new MainPresenter(view, repository, scheduler)

    when:
    sut.loadTrendingImages(0f)

    then:
    view.isActive()
    1 * view.addImages(response)
    1 * repository.getTrendingResults(_, _) >> Observable.just(response)
  }

  def "load trending images success"() {
    given:
    RiffsyResponse response = new RiffsyResponse()
    sut = new MainPresenter(view, repository, scheduler)

    when:
    sut.loadSearchImages("gifs", 0f)

    then:
    view.isActive()
    1 * view.addImages(response)
    1 * repository.getSearchResults(_, _, _) >> Observable.just(response)
  }
}
