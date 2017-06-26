package test

import org.junit.After
import org.junit.Before
import java.util.Random
import java.util.UUID.randomUUID

/**
 * JUnit Tests.
 *
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
abstract class TestBase {
  companion object {
    @JvmStatic val NUMBER_NEGATIVE_ONE = -1
    @JvmStatic val NUMBER_ZERO = 0
    @JvmStatic val NUMBER_ONE = 1
    @JvmStatic val DENSITY_LDPI = 36
    @JvmStatic val DENSITY_MDPI = 48
    @JvmStatic val DENSITY_HDPI = 72
    @JvmStatic val DENSITY_XHDPI = 96
    @JvmStatic val DENSITY_XXHDPI = 144
    @JvmStatic val DENSITY_XXXHDPI = 192
    @JvmStatic val STRING_EMPTY = ""
    @JvmStatic val STRING_NULL: String? = null
    @JvmStatic val STRING_UNIQUE = randomUUID().toString()
    @JvmStatic val STRING_UNIQUE2 = randomUUID().toString() + randomUUID().toString()
    @JvmStatic val STRING_UNIQUE3 = randomUUID().toString()
    @JvmStatic val INTEGER_RANDOM: Int? = Random().nextInt()
    @JvmStatic val INTEGER_RANDOM_POSITIVE: Int? = Random().nextInt(Integer.SIZE - 1)
    @JvmStatic val LONG_RANDOM: Long? = Random().nextLong()
    @JvmStatic val FLOAT_RANDOM: Float? = Random().nextFloat()
    @JvmStatic val DOUBLE_RANDOM: Double? = Random().nextDouble()
  }

  @Before @Throws(Throwable::class)
  open fun setUp() {
  }

  @After @Throws(Throwable::class)
  open fun tearDown() {
  }
}
