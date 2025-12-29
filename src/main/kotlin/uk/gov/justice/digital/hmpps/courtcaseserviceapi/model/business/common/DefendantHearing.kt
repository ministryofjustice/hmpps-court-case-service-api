package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.common

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.time.OffsetDateTime
import java.util.UUID

@Table(name = "defendant_hearing")
data class DefendantHearing(
  @Id
  @Column("id")
  val id: UUID,
  val defendantId: UUID?,
  val hearingId: UUID?,
  val createdAt: OffsetDateTime?,
  val createdBy: String?,
  val updatedAt: OffsetDateTime?,
  val updatedBy: String?,
  val isSoftDeleted: Boolean?,
  val version: Int?,
) : Serializable
