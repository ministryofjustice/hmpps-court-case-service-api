package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.defendant

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.ReadOnlyProperty
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.common.Address
import java.io.Serializable
import java.time.OffsetDateTime
import java.util.UUID

@Table("defendant")
data class Defendant(
  @Id
  @Column("id")
  val id: UUID?,
  @Column("offender_id")
  val offenderID: UUID?,
  @Column("legacy_id")
  val legacyId: Int?,
  @Column("defendant_id")
  val defendantID: UUID?,
  @Column("master_defendant_id")
  val masterDefendantID: UUID?,
  @Column("type")
  val type: String?,
  @Column("cpr_uuid")
  val cprUUID: UUID?,
  @Column("c_id")
  val cID: String?,
  @Column("pnc")
  val pncId: String?,
  @Column("cro")
  val croNumber: String?,
  @Column("crn")
  val crn: String?,
  @Column("person")
  val person: List<Person>?,
  @Column("address")
  val address: List<Address>?,
  @Column("is_youth")
  val isYouth: Boolean?,
  @ReadOnlyProperty
  @Column("tsv_name")
  val tsvName: String?,
  @Column("is_proceedings_concluded")
  val isProceedingsConcluded: Boolean?,
  @Column("is_offender_confirmed")
  val isOffenderConfirmed: Boolean?,
  @Column("is_manual_update")
  val isManualUpdate: Boolean?,
  @CreatedDate
  @Column("created_at")
  val createdAt: OffsetDateTime?,
  @Column("created_by")
  val createdBy: String?,
  @LastModifiedDate
  @Column("updated_at")
  val updatedAt: OffsetDateTime?,
  @Column("updated_by")
  val updatedBy: String?,
  @Column("is_soft_deleted")
  val isSoftDeleted: Boolean?,
  @Version
  val version: Int?,
) : Serializable
