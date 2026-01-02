package uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.hearing

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.mappers.HearingMapper.Companion.mapBusinessNoteToResponse
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.mappers.HearingMapper.Companion.mapRequestToBusinessNote
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.hearing.HearingCaseNoteRequest
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.api.hearing.HearingCaseNoteResponse
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.Hearing
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.HearingCaseNote
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.exceptions.HearingCaseNoteDraftNotFoundException
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.exceptions.HearingCaseNoteNotFoundException
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.exceptions.HearingNotFoundException
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.common.DefendantHearingRepository
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.hearing.HearingRepository
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

@Service
class HearingNotesServiceImpl(
  val hearingRepository: HearingRepository,
  val defendantHearingRepository: DefendantHearingRepository,
) : HearingNotesService {

  private companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  private var updateCounter = 0

  override fun addHearingCaseNoteAsDraft(hearingId: UUID, defendantId: UUID, requestNote: HearingCaseNoteRequest): Mono<HearingCaseNoteResponse> {
    val hearing = defendantHearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId)

    return hearing.switchIfEmpty(Mono.error(HearingNotFoundException(hearingId.toString())))
      .doOnNext { existingHearing ->
        log.info("Adding hearing case-note as a draft for hearingId ${existingHearing.id} and defendantId $defendantId")
      }
      .flatMap { existingHearing ->
        val existingCaseNote = existingHearing.hearingCaseNote
        val existingDraftCaseNote = findExistingDraftNoteForDefendant(existingCaseNote, defendantId, requestNote.createdByUUID)
        if (existingDraftCaseNote != null) return@flatMap Mono.just(mapBusinessNoteToResponse(existingDraftCaseNote))
        val newCaseNote = mapRequestToBusinessNote(defendantId, requestNote)

        val updatedNotes = if (existingCaseNote == null) {
          mutableListOf(newCaseNote)
        } else {
          existingCaseNote + newCaseNote
        }

        existingHearing.hearingCaseNote = updatedNotes
        hearingRepository.save(existingHearing).map { mapBusinessNoteToResponse(newCaseNote) }
      }
  }

  override fun updateHearingCaseNoteDraft(
    hearingId: UUID,
    defendantId: UUID,
    requestNote: HearingCaseNoteRequest,
  ): Mono<HearingCaseNoteResponse> {
    val hearing = defendantHearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId)
    updateCounter++

    return hearing.switchIfEmpty(Mono.error(HearingNotFoundException(hearingId.toString())))
      .doOnNext { existingHearing ->
        log.info(
          "Updating hearing case-note draft in hearingId ${existingHearing.id} for defendantId $defendantId",
        )
      }
      .flatMap { existingHearing ->
        val existingCaseNote = existingHearing.hearingCaseNote
        val matchingCaseNote = existingCaseNote?.find {
          it.defendantId == defendantId && it.createdByUUID == requestNote.createdByUUID && it.isDraft == true
        }

        if (matchingCaseNote == null) {
          return@flatMap Mono.error(
            HearingCaseNoteDraftNotFoundException(
              "Draft note not found for user %s on hearing %s with defendant %s",
              requestNote.createdByUUID.toString(),
              hearingId.toString(),
              defendantId.toString(),
            ),
          )
        }

        updateCaseNoteAndSaveToHearing(matchingCaseNote, requestNote, existingHearing, defendantId)
      }
  }

  override fun deleteHearingCaseNoteDraft(
    hearingId: UUID,
    defendantId: UUID,
    userUUID: UUID,
  ): Mono<Boolean> {
    val hearing = defendantHearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId)

    return hearing.switchIfEmpty(Mono.error(HearingNotFoundException(hearingId.toString())))
      .doOnNext { existingHearing ->
        log.info("Deleting HearingCaseNote draft in hearingId ${existingHearing.id} with user $userUUID for defendantId $defendantId")
      }
      .flatMap { existingHearing ->
        val existingCaseNote = existingHearing.hearingCaseNote
        val matchingCaseNote = existingCaseNote?.find {
          it.defendantId == defendantId && it.createdByUUID == userUUID && it.isDraft == true
        }

        if (matchingCaseNote == null) {
          return@flatMap Mono.error(
            HearingCaseNoteDraftNotFoundException(
              "Draft note not found for user %s on hearing %s with defendant %s",
              userUUID.toString(),
              hearingId.toString(),
              defendantId.toString(),
            ),
          )
        } else {
          matchingCaseNote.isSoftDeleted = true
          hearingRepository.save(existingHearing).thenReturn(matchingCaseNote.isSoftDeleted ?: true)
        }
      }
  }

  override fun updateHearingCaseNote(
    hearingId: UUID,
    defendantId: UUID,
    noteId: UUID,
    requestNote: HearingCaseNoteRequest,
  ): Mono<Boolean> {
    val hearing = defendantHearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId)

    return hearing.switchIfEmpty(Mono.error(HearingNotFoundException(hearingId.toString())))
      .doOnNext { existingHearing ->
        log.info("Updating HearingCaseNote in hearingId ${existingHearing.id} with user $noteId for defendantId $defendantId")
      }
      .flatMap { existingHearing ->
        val existingCaseNote = existingHearing.hearingCaseNote
        val matchingCaseNote = existingCaseNote?.find {
          it.defendantId == defendantId && it.createdByUUID == requestNote.createdByUUID && it.isDraft == false
        }

        if (matchingCaseNote == null) {
          return@flatMap Mono.error(
            HearingCaseNoteNotFoundException(
              noteId.toString(),
              defendantId.toString(),
              hearingId.toString(),
              requestNote.createdByUUID.toString(),
            ),
          )
        } else {
          val today = OffsetDateTime.now(ZoneOffset.UTC)
          matchingCaseNote.note = requestNote.note
          matchingCaseNote.author = requestNote.author
          matchingCaseNote.createdByUUID = requestNote.createdByUUID
          matchingCaseNote.updatedAt = today
          matchingCaseNote.updatedBy = requestNote.author
          matchingCaseNote.version = matchingCaseNote.version?.plus(1)

          hearingRepository.save(existingHearing).thenReturn(matchingCaseNote.updatedAt == (today ?: true))
        }
      }
  }

  override fun deleteHearingCaseNote(
    hearingId: UUID,
    defendantId: UUID,
    noteId: UUID,
    userUUID: UUID,
  ): Mono<Boolean> {
    val hearing = defendantHearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId)

    return hearing.switchIfEmpty(Mono.error(HearingNotFoundException(hearingId.toString())))
      .doOnNext { existingHearing ->
        log.info("Deleting HearingCaseNote in hearingId ${existingHearing.id} with user $userUUID for defendantId $defendantId")
      }
      .flatMap { existingHearing ->
        val existingCaseNote = existingHearing.hearingCaseNote
        val matchingCaseNote = existingCaseNote?.find { it.defendantId == defendantId && it.createdByUUID == userUUID && it.isDraft == false }

        if (matchingCaseNote == null) {
          return@flatMap Mono.error(
            HearingCaseNoteNotFoundException(
              noteId.toString(),
              defendantId.toString(),
              hearingId.toString(),
              userUUID.toString(),
            ),
          )
        } else {
          matchingCaseNote.isSoftDeleted = true
          // TODO add in telemetry service here
          hearingRepository.save(existingHearing).thenReturn(matchingCaseNote.isSoftDeleted ?: true)
        }
      }
  }

  private fun findExistingDraftNoteForDefendant(existingNotes: List<HearingCaseNote>?, defendantId: UUID, createdByUUID: UUID?): HearingCaseNote? = existingNotes?.find { note ->
    note.defendantId == defendantId && note.createdByUUID == createdByUUID && note.isDraft == true
  }

  private fun updateCaseNoteAndSaveToHearing(
    matchingCaseNote: HearingCaseNote,
    requestNote: HearingCaseNoteRequest,
    existingHearing: Hearing,
    defendantId: UUID,
  ): Mono<HearingCaseNoteResponse> {
    matchingCaseNote.note = requestNote.note
    matchingCaseNote.author = requestNote.author
    matchingCaseNote.createdByUUID = requestNote.createdByUUID
    matchingCaseNote.updatedAt = OffsetDateTime.now()
    if (updateCounter == 3) matchingCaseNote.isDraft = false

    // TODO if (matchingCaseNote.isDraft = false) call Telemetry service

    return hearingRepository.save(existingHearing)
      .map { savedHearing -> mapUpdatedCaseNoteToResponse(savedHearing, defendantId, requestNote) }
  }

  private fun mapUpdatedCaseNoteToResponse(
    savedHearing: Hearing,
    defendantId: UUID,
    requestNote: HearingCaseNoteRequest,
  ): HearingCaseNoteResponse {
    val updatedCaseNote = savedHearing.hearingCaseNote?.find {
      it.defendantId == defendantId && it.createdByUUID == requestNote.createdByUUID
    }
    return HearingCaseNoteResponse(
      noteId = updatedCaseNote?.id,
      note = updatedCaseNote?.note,
      createdAt = updatedCaseNote?.createdAt,
      author = updatedCaseNote?.author,
      createdByUuid = updatedCaseNote?.createdByUUID,
      isDraft = updatedCaseNote?.isDraft,
      isLegacy = updatedCaseNote?.isLegacy,
    )
  }
}
