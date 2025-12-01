package uk.gov.justice.digital.hmpps.courtcaseserviceapi.controller.features

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.client.FeatureFlagRequest
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.client.FeatureFlagResponse
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.FeatureFlagService

@RestController
@RequestMapping("/feature-flags")
class FeatureFlagController(
  private val featureFlagService: FeatureFlagService,
) {

  @PostMapping("/evaluate")
  fun evaluateFeatureFlag(
    @RequestBody body: FeatureFlagRequest,
  ): Mono<FeatureFlagResponse> = featureFlagService.isFeatureEnabled(
    flagKey = body.flagKey,
    context = body.context,
  )
}
