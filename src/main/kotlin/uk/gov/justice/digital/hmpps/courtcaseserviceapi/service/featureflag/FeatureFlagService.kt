package uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.featureflag

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.client.FeatureFlagClient
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.client.FeatureFlagRequest
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.client.FeatureFlagResponse

@Service
class FeatureFlagService(
  private val featureFlagClient: FeatureFlagClient,
) {

  fun isFeatureEnabled(flagKey: String, context: Map<String, String>? = null): Mono<FeatureFlagResponse> {
    val request = FeatureFlagRequest(
      entityId = flagKey,
      flagKey = flagKey,
      context = context,
    )
    return featureFlagClient.getFeatureFlags(request)
  }
}
