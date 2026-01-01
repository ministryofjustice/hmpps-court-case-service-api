package uk.gov.justice.digital.hmpps.courtcaseserviceapi.config

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class WireMockTestConfiguration {

  @Bean(initMethod = "start", destroyMethod = "stop")
  fun wireMockServer(): WireMockServer = WireMockServer(
    WireMockConfiguration.options()
      .port(8089)
      .usingFilesUnderDirectory("src/test/resources"),
  )
}
