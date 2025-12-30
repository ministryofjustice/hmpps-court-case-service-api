package uk.gov.justice.digital.hmpps.courtcaseserviceapi.integration

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.getBeansOfType
import org.springframework.context.ApplicationContext
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping
import java.io.File
import kotlin.jvm.java

class ResourceSecurityTest : IntegrationTestBase() {
  @Autowired
  private lateinit var context: ApplicationContext

  private val unprotectedDefaultMethods = setOf(
    "GET /v3/api-docs.yaml",
    "GET /swagger-ui.html",
    "GET /v3/api-docs",
    "GET /v3/api-docs/swagger-config",
    " /error",
  )

  @Test
  fun `Ensure all endpoints protected with PreAuthorize`() {
    // need to exclude any that are forbidden in helm configuration
    val exclusions = File("helm_deploy").walk().filter { it.name.equals("values.yaml") }.flatMap { file ->
      file.readLines().map { line ->
        line.takeIf { it.contains("location") }?.substringAfter("location ")?.substringBefore(" {")
      }
    }.filterNotNull().flatMap { path -> listOf("GET", "POST", "PUT", "DELETE").map { "$it $path" } }
      .toMutableSet().also {
        it.addAll(unprotectedDefaultMethods)
      }

    val beans = context.getBeansOfType<RequestMappingHandlerMapping>()
    beans.values.forEach { handlerMapping ->
      val handlerMethods = handlerMapping.handlerMethods
      handlerMethods.forEach { (mappingInfo, method) ->
        val classAnnotation = method.beanType.getAnnotation(PreAuthorize::class.java)
        val methodAnnotation = method.getMethodAnnotation(PreAuthorize::class.java)
        if (classAnnotation == null && methodAnnotation == null) {
          mappingInfo.directPaths.forEach { pattern ->
            assertThat(exclusions.contains(pattern)).`as`("Only exclusion '$pattern'")
              .withFailMessage {
                "Found $mappingInfo of type $method with no PreAuthorize annotation"
              }.isTrue
          }
        }
      }
    }
  }
}
