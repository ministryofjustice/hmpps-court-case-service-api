package uk.gov.justice.digital.hmpps.courtcaseserviceapi.controller.hearings

import com.fasterxml.uuid.impl.UUIDUtil
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.config.security.AuthenticationExtractor
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.hearing.HearingCaseNoteRequest
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.hearing.HearingCaseNoteResponse
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.hearing.HearingService
import java.security.Principal
import java.util.UUID

@RestController
@RequestMapping("/hearing/{hearingId}")
class HearingController(private val hearingService: HearingService, private val authenticationExtractor: AuthenticationExtractor) {

  @PostMapping(
    value = ["/defendant/{defendantId}/note"],
    consumes = [MediaType.APPLICATION_JSON_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE],
  )
  fun addHearingCaseNoteAsDraftToHearing(
    @PathVariable hearingId: UUID,
    @PathVariable defendantId: UUID,
    @RequestBody requestNote: Mono<HearingCaseNoteRequest>,
    @AuthenticationPrincipal principal: Principal,
  ): Mono<ResponseEntity<HearingCaseNoteResponse>> = requestNote.flatMap { request ->
    request.createdByUUID = UUIDUtil.uuid(authenticationExtractor.extractAuthUserUuid(principal))
    hearingService.addHearingCaseNoteAsDraft(hearingId, defendantId, request)
  }.map { response -> ResponseEntity.status(CREATED).body(response) }
    .defaultIfEmpty(ResponseEntity.noContent().build())

  @PutMapping(
    value = ["/defendant/{defendantId}/notes/draft"],
    consumes = [MediaType.APPLICATION_JSON_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE],
  )
  fun updateHearingCaseNoteDraftToHearing(
    @PathVariable hearingId: UUID,
    @PathVariable defendantId: UUID,
    @RequestBody note: Mono<HearingCaseNoteRequest>,
    @AuthenticationPrincipal principal: Principal,
  ): Mono<ResponseEntity<HearingCaseNoteResponse>> = note.flatMap { request ->
    request.createdByUUID = UUIDUtil.uuid(authenticationExtractor.extractAuthUserUuid(principal))
    hearingService.updateHearingCaseNoteDraft(hearingId, defendantId, request)
  }.map { response -> ResponseEntity.ok().body(response) }
    .defaultIfEmpty(ResponseEntity.notFound().build())

  @DeleteMapping(value = ["/defendant/{defendantId}/notes/draft"])
  fun deleteHearingCaseNoteDraft(
    @PathVariable hearingId: UUID,
    @PathVariable defendantId: UUID,
    @AuthenticationPrincipal principal: Principal,
  ): Mono<ResponseEntity<Void>> = hearingService.deleteHearingCaseNoteDraft(hearingId, defendantId, UUIDUtil.uuid(authenticationExtractor.extractAuthUserUuid(principal)))
    .filter { bool -> bool }
    .map { _ -> ResponseEntity.ok().build<Void>() }
    .defaultIfEmpty(ResponseEntity.notFound().build())
}
