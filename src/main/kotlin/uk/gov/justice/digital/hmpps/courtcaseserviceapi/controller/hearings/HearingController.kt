package uk.gov.justice.digital.hmpps.courtcaseserviceapi.controller.hearings

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.hearing.HearingCaseNote
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.hearing.HearingService

@RestController
@RequestMapping("/hearing/{hearingId}")
class HearingController(
  private val hearingService: HearingService,
) {
  @PostMapping("/defendant/{defendantId}/note")
  fun addNote(
    @PathVariable hearingId: String,
    @PathVariable defendantId: String,
    @RequestBody note: HearingCaseNote
  ): Mono<HearingCaseNote> {
    return hearingService.addNote(hearingId, defendantId, note)
  }
}