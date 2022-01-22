package com.burrowsapps.example.gif.data.source.network

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.squareup.moshi.Json
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.reflections.Reflections
import org.reflections.scanners.MemberUsageScanner
import org.reflections.scanners.MethodParameterNamesScanner
import org.reflections.scanners.Scanners
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder
import java.lang.reflect.Modifier

@RunWith(AndroidJUnit4::class)
class DtoConstructorTest {
  private val modelPackage = "burrows.apps.example.gif.data.rest.model"
  private val dataTransportObject = "Dto"
  private lateinit var classes: Set<Class<*>>

  @Before
  fun setUp() {
    val reflections = Reflections(
      ConfigurationBuilder().filterInputsBy(
        FilterBuilder().includePackage(modelPackage)
      ).setUrls(ClasspathHelper.forClassLoader())
        .setScanners(
          Scanners.SubTypes,
          Scanners.TypesAnnotated,
          Scanners.FieldsAnnotated,
          Scanners.MethodsAnnotated,
          Scanners.MethodsParameter,
          MethodParameterNamesScanner(),
          MemberUsageScanner()
        )
    )
    classes = reflections.getSubTypesOf(Any::class.java)
  }

  @Test
  fun testMakeSureAllDTOsHaveNoArgsPublicConstructors() {
    classes.forEach { clazz ->
      val className = clazz.name
      if (className.endsWith(dataTransportObject) && !hasNoArgConstructor(clazz)) {
        fail("Found no 'no-args' and public constructors in class $className")
      }
    }
  }

  @Test
  fun testMakeSureAllHaveNoFinalAnnotations() {
    classes.forEach { clazz ->
      val className = clazz.name
      if (className.endsWith(dataTransportObject) && hasNoFinalAnnotations(clazz)) {
        fail("Found finalized field with @SerializedNamed annotation in class $className")
      }
    }
  }

  /**
   * Make sure each DTO has a single no-args and public constructor:
   * public class Blah {
   *   public Blah() {
   *   }
   * }
   */
  private fun hasNoArgConstructor(klass: Class<*>): Boolean {
    return klass.declaredConstructors.any { field ->
      field.parameterTypes.isEmpty() && Modifier.isPublic(field.modifiers)
    }
  }

  /**
   * None of this:
   * @Json
   * private final String blah;
   */
  private fun hasNoFinalAnnotations(klass: Class<*>): Boolean {
    return klass.declaredFields.any { field ->
      field.getAnnotation(Json::class.java) != null && Modifier.isFinal(field.modifiers)
    }
  }
}
