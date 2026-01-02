package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.defendant

import org.springframework.data.annotation.Id
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
  val offenderID: UUID?,
  val legacyId: Int?,
  val defendantID: UUID?,
  val masterDefendantID: UUID?,
  val type: String?,
  val cprUUID: String?,
  val cID: String?,
  val pncId: String?,
  val croNumber: String?,
  val crn: String?,

  @Column("person")
  val person: List<Person>?,

  @Column("address")
  val address: List<Address>?,
  val isYouth: Boolean?,
  val tsvName: String?,
  val isProceedingsConcluded: Boolean?,
  val isOffenderConfirmed: Boolean?,
  val isManualUpdate: Boolean?,
  val createdAt: OffsetDateTime?,
  val createdBy: String?,
  val updatedAt: OffsetDateTime?,
  val updatedBy: String?,
  val isSoftDeleted: Boolean?,
  val version: Int?,
) : Serializable
