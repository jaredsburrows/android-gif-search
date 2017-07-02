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
@Suppress("unused")
abstract class AndroidTestBase {
  companion object {
    val NUMBER_NEGATIVE_ONE = -1
    val NUMBER_ZERO = 0
    val NUMBER_ONE = 1
    val DENSITY_LDPI = 36
    val DENSITY_MDPI = 48
    val DENSITY_HDPI = 72
    val DENSITY_XHDPI = 96
    val DENSITY_XXHDPI = 144
    val DENSITY_XXXHDPI = 192
    val STRING_EMPTY = ""
    val STRING_NULL: String? = null
    val STRING_UNIQUE = randomUUID().toString()
    val STRING_UNIQUE2 = randomUUID().toString() + randomUUID().toString()
    val STRING_UNIQUE3 = randomUUID().toString()
    val INTEGER_RANDOM: Int = Random().nextInt()
    val INTEGER_RANDOM_POSITIVE: Int = Random().nextInt(Integer.SIZE - 1)
    val LONG_RANDOM: Long = Random().nextLong()
    val FLOAT_RANDOM: Float = Random().nextFloat()
    val DOUBLE_RANDOM: Double = Random().nextDouble()
  }

  @Before open fun setUp() {
  }

  @After open fun tearDown() {
  }
}
