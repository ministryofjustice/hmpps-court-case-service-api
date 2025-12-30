package uk.gov.justice.digital.hmpps.courtcaseserviceapi.config.security

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientSecurityConfiguration {

  private companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @Bean
  fun authorizedClientManager(
    clientRegistrationRepository: ReactiveClientRegistrationRepository,
    authorizedClientRepository: ServerOAuth2AuthorizedClientRepository,
  ): ReactiveOAuth2AuthorizedClientManager {
    val authorizedClientProvider = ReactiveOAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().authorizationCode().build()
    val authorizedClientManager = DefaultReactiveOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository)
    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider)
    return authorizedClientManager
  }

  @Bean
  fun webClient(authorizedClientManager: ReactiveOAuth2AuthorizedClientManager): WebClient {
    log.info("Configuring WebClient")
    val oauth2 = ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager)

    return WebClient.builder().filter(oauth2).build()
  }
}
