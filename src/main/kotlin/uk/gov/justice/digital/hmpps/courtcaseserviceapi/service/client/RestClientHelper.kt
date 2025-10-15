package uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.client

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.util.function.Function

class RestClientHelper {
    private val client: WebClient? = null
    private val oauthClient: String? = null
    private val disableAuthentication: Boolean? = null

    private companion object {
      val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }

    fun delete(path: String): WebClient.RequestHeadersSpec<*>? {
        val spec: WebClient.RequestHeadersSpec<*> = client!!
            .delete()
            .uri(Function { uriBuilder: UriBuilder? ->
                uriBuilder!!
                    .path(path)
                    .build()
            }
            )
            .accept(MediaType.APPLICATION_JSON)

        return addSpecAuthAttribute(spec, path)
    }

    @JvmOverloads
    fun get(
        path: String,
        queryParams: MultiValueMap<String?, String?> = LinkedMultiValueMap<String?, String?>(0)
    ): WebClient.RequestHeadersSpec<*>? {
        val spec: WebClient.RequestHeadersSpec<*> = client!!
            .get()
            .uri(Function { uriBuilder: UriBuilder? ->
                uriBuilder!!
                    .path(path)
                    .queryParams(queryParams)
                    .build()
            }
            )
            .accept(MediaType.APPLICATION_JSON)

        return addSpecAuthAttribute(spec, path)
    }

    fun get(path: String, mediaType: MediaType): WebClient.RequestHeadersSpec<*>? {
        val spec: WebClient.RequestHeadersSpec<*> = client!!
            .get()
            .uri(Function { uriBuilder: UriBuilder? ->
                uriBuilder!!
                    .path(path)
                    .build()
            }
            )
            .accept(mediaType)

        return addSpecAuthAttribute(spec, path)
    }

    private fun addSpecAuthAttribute(
        spec: WebClient.RequestHeadersSpec<*>,
        path: String?
    ): WebClient.RequestHeadersSpec<*>? {
        if (disableAuthentication == true) {
            LOG.info(String.format("Skipping authentication with community api for call to %s", path))
            return spec
        }

        LOG.info(String.format("Authenticating with %s for call to %s", oauthClient, path))
        return spec.attributes(ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId(oauthClient))
    }

    fun handleOffenderError(crn: String?, clientResponse: ClientResponse): Mono<out Throwable?> {
        if (HttpStatus.NOT_FOUND == clientResponse.statusCode()) {
            // return Mono.error(new OffenderNotFoundException(crn));
        }
        if (HttpStatus.FORBIDDEN == clientResponse.statusCode()) {
//            return clientResponse.bodyToMono(CommunityApiError.class)
//                    .flatMap((error) -> Mono.error(new ForbiddenException(error.getDeveloperMessage())));
        }
        return handleError(clientResponse)
    }

    private fun handleError(clientResponse: ClientResponse): Mono<out Throwable?> {
        val httpStatusCode = clientResponse.statusCode()

        return Mono.error<Throwable?>(
            WebClientResponseException.create(
                httpStatusCode.value(),
                httpStatusCode.toString(),
                clientResponse.headers().asHttpHeaders(),
                clientResponse.toString().toByteArray(),
                StandardCharsets.UTF_8
            )
        )
    }
}
