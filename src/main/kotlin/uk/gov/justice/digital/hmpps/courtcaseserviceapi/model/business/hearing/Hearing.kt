package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.time.OffsetDateTime
import java.util.UUID

@Table("hearing")
data class Hearing(
  @Id
  @Column("id")
  val id: UUID?,
  val hearingID: UUID,
  val legacyID: Int?,
  val type: String?,
  val eventType: String?,
  val listNumber: String?,
  val prepStatus: String?,
  val isHearingOutcomeNotRequired: Boolean?,
  val firstCreatedAt: OffsetDateTime?,
  @Column("hearing_outcome")
  val hearingOutcome: HearingOutcome? = null,
  @Column("hearing_case_note")
  var hearingCaseNote: HearingCaseNote? = null,
  val createdAt: OffsetDateTime?,
  val createdBy: String?,
  val updatedAt: OffsetDateTime?,
  val updatedBy: String?,
  val isSoftDeleted: Boolean?,
  val version: Int?,
) : Serializable
