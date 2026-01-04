package uk.gov.justice.digital.hmpps.courtcaseserviceapi.config

import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.exceptions.ApiException
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.exceptions.ClientException
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.exceptions.ServerException
import java.time.Duration

@Component
class OAuth2WebClientService(private val webClient: WebClient) {

  fun <T, R> post(url: String, requestBody: T, clientRegistrationId: String, responseType: Class<R>): Mono<R> = webClient.post().uri(url)
    .attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId(clientRegistrationId))
    .bodyValue(requestBody!!)
    .retrieve()
    .bodyToMono(responseType)
    .retryWhen(
      Retry.backoff(3, Duration.ofSeconds(2))
        .filter(this::isRetryableException),
    )
    .onErrorResume(this::handleError)

  fun <R> post(
    url: String,
    requestBody: MultiValueMap<String, HttpEntity<*>>,
    clientRegistrationId: String,
    responseType: Class<R>,
  ): Mono<R> = webClient.post()
    .uri(url)
    .attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId(clientRegistrationId))
    .contentType(MediaType.MULTIPART_FORM_DATA)
    .body(BodyInserters.fromMultipartData(requestBody))
    .retrieve()
    .bodyToMono(responseType)
    .retryWhen(
      Retry.backoff(3, Duration.ofSeconds(2))
        .filter(this::isRetryableException),
    )
    .onErrorResume(this::handleError)

  fun <T, R> put(url: String, requestBody: T, clientRegistrationId: String, responseType: Class<R>): Mono<R> = webClient.put().uri(url)
    .attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId(clientRegistrationId))
    .bodyValue(requestBody!!)
    .retrieve()
    .bodyToMono(responseType)
    .retryWhen(
      Retry.backoff(3, Duration.ofSeconds(2))
        .filter(this::isRetryableException),
    )
    .onErrorResume(this::handleError)

  fun <T> get(url: String, clientRegistrationId: String, responseType: Class<T>): Mono<T> = webClient.get().uri(url)
    .attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId(clientRegistrationId))
    .retrieve()
    .bodyToMono(responseType)
    .retryWhen(
      Retry.backoff(3, Duration.ofSeconds(2))
        .filter(this::isRetryableException),
    )
    .onErrorResume(this::handleError)

  fun <T> delete(url: String, clientRegistrationId: String, responseType: Class<T>): Mono<T> = webClient.delete().uri(url)
    .attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId(clientRegistrationId))
    .retrieve()
    .bodyToMono(responseType)
    .retryWhen(
      Retry.backoff(3, Duration.ofSeconds(2))
        .filter(this::isRetryableException),
    )
    .onErrorResume(this::handleError)

  fun deleteWithoutResponse(url: String, clientRegistrationId: String): Mono<Void> = webClient.delete().uri(url)
    .attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId(clientRegistrationId))
    .retrieve()
    .bodyToMono<Void>()
    .retryWhen(
      Retry.backoff(3, Duration.ofSeconds(2))
        .filter(this::isRetryableException),
    )
    .onErrorResume(this::handleError)

  private fun isRetryableException(throwable: Throwable): Boolean {
    if (throwable is WebClientResponseException) {
      val status = HttpStatus.resolve(throwable.statusCode.value())
      return status != null && (status.is5xxServerError || status == HttpStatus.REQUEST_TIMEOUT)
    }
    return false
  }

  private fun <T> handleError(error: Throwable): Mono<T> {
    if (error is WebClientResponseException) {
      val status = HttpStatus.resolve(error.statusCode.value())

      if (status != null) {
        return when (status.series()) {
          HttpStatus.Series.CLIENT_ERROR ->
            Mono.error(
              ClientException(
                "Client error occurred: ${error.statusCode} - ${error.responseBodyAsString}",
                error,
              ),
            )

          HttpStatus.Series.SERVER_ERROR ->
            Mono.error(
              ServerException(
                "Server error occurred: ${error.statusCode} - ${error.responseBodyAsString}",
                error,
              ),
            )

          else -> Mono.error(ApiException("Unexpected error occurred: ${error.statusCode}", error))
        }
      }
    }

    return Mono.error(ApiException("Unexpected error occurred", error))
  }
}
