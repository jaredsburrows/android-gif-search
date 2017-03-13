package test

import spock.lang.Specification

import static java.lang.Integer.SIZE
import static java.util.UUID.randomUUID

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
abstract class BaseSpec extends Specification {
  // Testing Comparisons
  protected static final int NUMBER_NEGATIVE_ONE = -1
  protected static final int NUMBER_ZERO = 0
  protected static final int NUMBER_ONE = 1
  protected static final int DENSITY_LDPI = 36
  protected static final int DENSITY_MDPI = 48
  protected static final int DENSITY_HDPI = 72
  protected static final int DENSITY_XHDPI = 96
  protected static final int DENSITY_XXHDPI = 144
  protected static final int DENSITY_XXXHDPI = 192
  protected static final String STRING_EMPTY = ""
  protected static final String STRING_NULL = null
  protected static final String STRING_UNIQUE = randomUUID().toString()
  protected static final String STRING_UNIQUE2 = randomUUID().toString() + randomUUID().toString()
  protected static final String STRING_UNIQUE3 = randomUUID().toString()
  protected static final Integer INTEGER_RANDOM = new Random().nextInt()
  protected static final Integer INTEGER_RANDOM_POSITIVE = new Random().nextInt(SIZE - 1)
  protected static final Float FLOAT_RANDOM = new Random().nextFloat();
  protected static final Long LONG_RANDOM = new Random().nextLong()
  protected static final Double DOUBLE_RANDOM = new Random().nextDouble()
}
