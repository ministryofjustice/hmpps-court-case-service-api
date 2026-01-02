package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.time.OffsetDateTime
import java.util.UUID

@Table("hearing")
data class Hearing(
  @Id
  val id: UUID? = null,

  @Column("legacy_id")
  val legacyId: Int? = null,

  @Column("hearing_id")
  val hearingId: UUID? = null,

  @Column("type")
  val type: String? = null,

  @Column("event_type")
  val eventType: String? = null,

  @Column("list_number")
  val listNumber: String? = null,

  @Column("prep_status")
  val prepStatus: String? = null,

  @Column("is_hearing_outcome_not_required")
  val isHearingOutcomeNotRequired: Boolean? = null,

  @Column("first_created")
  val firstCreated: OffsetDateTime? = null,

  @Column("hearing_outcome")
  val hearingOutcome: List<HearingOutcome>? = null,

  @Column("hearing_case_note")
  var hearingCaseNote: List<HearingCaseNote>? = null,

  @CreatedDate
  @Column("created_at")
  val createdAt: OffsetDateTime? = null,

  @Column("created_by")
  val createdBy: String? = null,

  @LastModifiedDate
  @Column("updated_at")
  val updatedAt: OffsetDateTime? = null,

  @Column("updated_by")
  val updatedBy: String? = null,

  @Column("is_soft_deleted")
  val isSoftDeleted: Boolean = false,

  @Version
  val version: Int = 0,
) : Serializable
