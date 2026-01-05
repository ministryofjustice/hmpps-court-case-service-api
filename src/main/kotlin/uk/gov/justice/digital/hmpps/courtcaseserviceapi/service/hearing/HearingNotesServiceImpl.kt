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
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.hearing.HearingRepository
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Service
class HearingNotesServiceImpl(
  private val hearingRepository: HearingRepository,
) : HearingNotesService {

  private companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  private val draftUpdateCounters = ConcurrentHashMap<UUID, AtomicInteger>()

  override fun addHearingCaseNoteAsDraft(
    hearingId: UUID,
    defendantId: UUID,
    requestNote: HearingCaseNoteRequest,
  ): Mono<HearingCaseNoteResponse> {
    return findHearingOrFail(defendantId, hearingId)
      .doOnNext { log.info("Adding hearing case-note as a draft for hearingId ${it.id} and defendantId $defendantId") }
      .flatMap { existingHearing ->
        val existingCaseNotes = existingHearing.hearingCaseNote
        val existingDraft = findExistingDraftNoteForDefendant(existingCaseNotes, defendantId, requestNote.createdByUUID)
        if (existingDraft != null) {
          return@flatMap Mono.just(mapBusinessNoteToResponse(existingDraft))
        }

        val newCaseNote = mapRequestToBusinessNote(defendantId, requestNote)
        val updatedNotes = (existingCaseNotes ?: emptyList()) + newCaseNote

        saveWithUpdatedNotes(existingHearing, updatedNotes)
          .map { mapBusinessNoteToResponse(newCaseNote) }
      }
  }

  override fun updateHearingCaseNoteDraft(
    hearingId: UUID,
    defendantId: UUID,
    requestNote: HearingCaseNoteRequest,
  ): Mono<HearingCaseNoteResponse> {
    return findHearingOrFail(defendantId, hearingId)
      .flatMap { existingHearing ->
        val matchingCaseNote = existingHearing.hearingCaseNote?.find {
          it.defendantId == defendantId &&
            it.createdByUUID == requestNote.createdByUUID &&
            it.isDraft == true
        } ?: return@flatMap Mono.error(
          buildDraftNotFoundException(hearingId, defendantId, requestNote.createdByUUID),
        )

        val updateCount = matchingCaseNote.id?.let {
          draftUpdateCounters
            .computeIfAbsent(it) { AtomicInteger(0) }
        }
          ?.incrementAndGet()

        val shouldPublish = updateCount!! >= 3
        if (shouldPublish) {
          draftUpdateCounters.remove(matchingCaseNote.id)
        }

        val updatedNote = matchingCaseNote.copy(
          note = requestNote.note,
          author = requestNote.author,
          updatedAt = OffsetDateTime.now(),
          updatedBy = requestNote.author,
          version = (matchingCaseNote.version ?: 0) + 1,
          isDraft = !shouldPublish,
        )

        val updatedNotes = existingHearing.hearingCaseNote?.map {
          if (it.id == matchingCaseNote.id) updatedNote else it
        } ?: listOf(updatedNote)

        saveWithUpdatedNotes(existingHearing, updatedNotes)
          .map { mapBusinessNoteToResponse(updatedNote) }
      }
  }

  override fun deleteHearingCaseNoteDraft(
    hearingId: UUID,
    defendantId: UUID,
    userUUID: UUID,
  ): Mono<Boolean> {
    return findHearingOrFail(defendantId, hearingId)
      .doOnNext { log.info("Deleting HearingCaseNote draft in hearingId ${it.id} with user $userUUID for defendantId $defendantId") }
      .flatMap { existingHearing ->
        val existingCaseNotes = existingHearing.hearingCaseNote
        val matchingCaseNote = existingCaseNotes?.find {
          it.defendantId == defendantId && it.createdByUUID == userUUID && it.isDraft == true
        }

        if (matchingCaseNote == null) {
          return@flatMap Mono.error(buildDraftNotFoundException(hearingId, defendantId, userUUID))
        }

        val updatedNote = matchingCaseNote.copy(
          isSoftDeleted = true,
          updatedAt = OffsetDateTime.now(),
          updatedBy = userUUID.toString(),
        )

        val updatedNotes = existingCaseNotes.map {
          if (it.id == matchingCaseNote.id) updatedNote else it
        }

        saveWithUpdatedNotes(existingHearing, updatedNotes)
          .thenReturn(true)
      }
  }

  override fun updateHearingCaseNote(
    hearingId: UUID,
    defendantId: UUID,
    noteId: UUID,
    requestNote: HearingCaseNoteRequest,
  ): Mono<Boolean> {
    return findHearingOrFail(defendantId, hearingId)
      .doOnNext { log.info("Updating HearingCaseNote in hearingId ${it.id} with user $noteId for defendantId $defendantId") }
      .flatMap { existingHearing ->
        val existingCaseNotes = existingHearing.hearingCaseNote
        val matchingCaseNote = existingCaseNotes?.find {
          it.id == noteId &&
            it.defendantId == defendantId &&
            it.createdByUUID == requestNote.createdByUUID &&
            it.isDraft == false
        }

        if (matchingCaseNote == null) {
          return@flatMap Mono.error(
            buildPublishedNotFoundException(noteId, defendantId, hearingId, requestNote.createdByUUID),
          )
        }

        val updatedNote = matchingCaseNote.copy(
          note = requestNote.note,
          author = requestNote.author,
          createdByUUID = requestNote.createdByUUID,
          updatedAt = OffsetDateTime.now(ZoneOffset.UTC),
          updatedBy = requestNote.author,
          version = (matchingCaseNote.version ?: 0) + 1,
        )

        val updatedNotes = existingCaseNotes.map {
          if (it.id == matchingCaseNote.id) updatedNote else it
        }

        saveWithUpdatedNotes(existingHearing, updatedNotes)
          .thenReturn(true)
      }
  }

  override fun deleteHearingCaseNote(
    hearingId: UUID,
    defendantId: UUID,
    noteId: UUID,
    userUUID: UUID,
  ): Mono<Boolean> {
    return findHearingOrFail(defendantId, hearingId)
      .doOnNext { log.info("Deleting HearingCaseNote in hearingId ${it.id} with user $userUUID for defendantId $defendantId") }
      .flatMap { existingHearing ->
        val existingCaseNotes = existingHearing.hearingCaseNote
        val matchingCaseNote = existingCaseNotes?.find {
          it.id == noteId && it.defendantId == defendantId && it.createdByUUID == userUUID && !it.isDraft!!
        }

        if (matchingCaseNote == null) {
          return@flatMap Mono.error(
            buildPublishedNotFoundException(noteId, defendantId, hearingId, userUUID),
          )
        }

        val updatedNote = matchingCaseNote.copy(
          isSoftDeleted = true,
          updatedAt = OffsetDateTime.now(),
          updatedBy = userUUID.toString(),
        )

        val updatedNotes = existingCaseNotes.map {
          if (it.id == matchingCaseNote.id) updatedNote else it
        }

        saveWithUpdatedNotes(existingHearing, updatedNotes)
          .thenReturn(true)
      }
  }

  private fun findExistingDraftNoteForDefendant(existingNotes: List<HearingCaseNote>?, defendantId: UUID, createdByUUID: UUID?): HearingCaseNote? = existingNotes?.find { note ->
    note.defendantId == defendantId && note.createdByUUID == createdByUUID && note.isDraft == true
  }

  private fun findHearingOrFail(defendantId: UUID, hearingId: UUID): Mono<Hearing> = hearingRepository.findByDefendantIdAndHearingId(defendantId, hearingId)
    .switchIfEmpty(Mono.error(HearingNotFoundException(hearingId.toString())))

  private fun saveWithUpdatedNotes(existingHearing: Hearing, updatedNotes: List<HearingCaseNote>): Mono<Hearing> = hearingRepository.save(existingHearing.copy(hearingCaseNote = updatedNotes))

  private fun buildDraftNotFoundException(
    hearingId: UUID,
    defendantId: UUID,
    userUuid: UUID?,
  ): HearingCaseNoteDraftNotFoundException = HearingCaseNoteDraftNotFoundException(
    "Draft note not found for user %s on hearing %s with defendant %s",
    userUuid?.toString() ?: "unknown",
    hearingId.toString(),
    defendantId.toString(),
  )

  private fun buildPublishedNotFoundException(
    noteId: UUID,
    defendantId: UUID,
    hearingId: UUID,
    userUuid: UUID?,
  ): HearingCaseNoteNotFoundException = HearingCaseNoteNotFoundException(
    noteId.toString(),
    defendantId.toString(),
    hearingId.toString(),
    userUuid?.toString() ?: "unknown",
  )
}
