package test

import com.burrowsapps.example.gif.CoroutineDispatcherProvider
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class TestCoroutineDispatcherProvider @Inject constructor() : CoroutineDispatcherProvider() {
  override fun io() = Dispatchers.Unconfined
  override fun ui() = Dispatchers.Unconfined
}
