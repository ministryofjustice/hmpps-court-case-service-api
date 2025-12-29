package uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.hearing

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.mappers.HearingMapper.Companion.convertBusinessNoteToResponse
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.mappers.HearingMapper.Companion.convertRequestNoteToBusiness
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.hearing.HearingCaseNoteRequest
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.hearing.HearingCaseNoteResponse
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.Hearing
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.HearingCaseNote
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.exceptions.HearingCaseNoteDraftNotFoundException
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.exceptions.HearingNotFoundException
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.common.DefendantHearingRepository
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.hearing.HearingRepository
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

@Service
class HearingServiceImpl(
  val hearingRepository: HearingRepository,
  val defendantHearingRepository: DefendantHearingRepository,
) : HearingService {

  private companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  private var updateCounter = 0

  override fun addHearingCaseNoteAsDraft(hearingId: UUID, defendantId: UUID, requestNote: HearingCaseNoteRequest): Mono<HearingCaseNoteResponse> {
    val hearing = defendantHearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId)
    val requestToBusinessNote = convertRequestNoteToBusiness(requestNote)

    log.info("Adding hearing case-note as a draft for hearingId as $hearing and defendantId as $defendantId")

    return hearing.switchIfEmpty(Mono.error(HearingNotFoundException(hearingId)))
      .filter { existingHearing -> existingHearing.hearingCaseNote == null }
      .flatMap { existingHearing ->
        val existingCaseNote = existingHearing.hearingCaseNote

        if (requestToBusinessNote == existingCaseNote) {
          Mono.just(convertBusinessNoteToResponse(existingCaseNote))
        } else {
          saveDraftAndTransformToResponse(existingHearing, requestToBusinessNote)
        }
      }
  }

  override fun updateHearingCaseNoteDraft(
    hearingId: UUID,
    defendantId: UUID,
    requestNote: HearingCaseNoteRequest,
  ): Mono<HearingCaseNoteResponse> {
    val hearing = defendantHearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId)
    val requestToBusinessNote = convertRequestNoteToBusiness(requestNote)
    updateCounter++

    log.info("Updating hearing case-note draft in hearingId $hearingId for defendantId $defendantId")

    return hearing.switchIfEmpty(Mono.error(HearingNotFoundException(hearingId)))
      .filter { existingHearing -> existingHearing.hearingCaseNote != null }
      .filter { existingHearing -> existingHearing.hearingCaseNote?.isSoftDeleted == false && existingHearing.hearingCaseNote?.isDraft == true }
      .flatMap { existingHearing ->
        val existingCaseNote = existingHearing.hearingCaseNote

        if (requestToBusinessNote == existingCaseNote) {
          Mono.just(convertBusinessNoteToResponse(existingCaseNote))
        } else {
          updateDraftAndTransformToResponse(existingCaseNote, requestToBusinessNote, existingHearing)
        }
      }
  }

  override fun deleteHearingCaseNoteDraft(
    hearingId: UUID,
    defendantId: UUID,
    userUUID: UUID,
  ): Mono<Boolean> {
    val hearing = defendantHearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId)

    log.info("Deleting HearingCaseNote draft in hearingId $hearingId with user $userUUID for defendantId $defendantId")

    return hearing.switchIfEmpty(Mono.error(HearingNotFoundException(hearingId)))
      .filter { existingHearing -> existingHearing.hearingCaseNote != null }
      .filter { existingHearing -> existingHearing.hearingCaseNote?.isSoftDeleted == false && existingHearing.hearingCaseNote?.isDraft == true }
      .flatMap { existingHearing ->
        val existingCaseNoteCreatedByUUID = existingHearing.hearingCaseNote?.createdByUUID

        if (existingCaseNoteCreatedByUUID != userUUID) {
          return@flatMap Mono.error(
            HearingCaseNoteDraftNotFoundException(
              "Draft note not found for user %s on hearing %s with defendant %s",
              userUUID,
              hearingId,
              defendantId,
            ),
          )
        } else {
          existingHearing.hearingCaseNote?.isSoftDeleted = true
          hearingRepository.save(existingHearing).thenReturn(existingHearing.hearingCaseNote?.isSoftDeleted)
        }
      }
  }

  private fun saveDraftAndTransformToResponse(
    existingHearing: Hearing,
    requestToBusinessNote: HearingCaseNote,
  ): Mono<HearingCaseNoteResponse> {
    log.info("Saving draft and transform to response for $requestToBusinessNote")
    existingHearing.hearingCaseNote = requestToBusinessNote
    return hearingRepository.save(existingHearing).map { savedHearing -> convertBusinessNoteToResponse(savedHearing.hearingCaseNote) }
  }

  private fun updateDraftAndTransformToResponse(
    existingCaseNote: HearingCaseNote?,
    requestToBusinessNote: HearingCaseNote,
    existingHearing: Hearing,
  ): Mono<HearingCaseNoteResponse> {
    log.info("Updating hearingCaseNote draft and transform to response for $existingCaseNote")

    existingCaseNote?.note = requestToBusinessNote.note
    existingCaseNote?.author = requestToBusinessNote.author
    existingCaseNote?.createdByUUID = requestToBusinessNote.createdByUUID
    existingCaseNote?.updatedAt = OffsetDateTime.now(ZoneOffset.UTC)
    existingCaseNote?.updatedBy = requestToBusinessNote.author
    existingCaseNote?.version = requestToBusinessNote.version?.plus(1)

    if (updateCounter == 3) existingCaseNote?.isDraft = false

    // TODO if (existingCaseNote?.isDraft = false) call Telemetry service

    return hearingRepository.save(existingHearing)
      .map { savedHearing -> convertBusinessNoteToResponse(savedHearing.hearingCaseNote) }
  }
}
