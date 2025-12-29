package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.offender

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.time.OffsetDateTime
import java.util.UUID

@Table("offender_match")
data class OffenderMatch(
  @Id
  @Column("id")
  val id: UUID?,
  val offenderId: Int?,
  val offenderMatchGroupId: Int?,
  val matchType: String?,
  val aliases: String?,
  val isRejected: Boolean?,
  val matchProbability: Float?,
  val createdAt: OffsetDateTime?,
  val createdBy: String?,
  val updatedAt: OffsetDateTime?,
  val updatedBy: String?,
  val isSoftDeleted: Boolean?,
  val version: Int?,
) : Serializable
