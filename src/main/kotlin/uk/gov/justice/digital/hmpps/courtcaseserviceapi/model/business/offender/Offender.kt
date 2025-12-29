package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.offender

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@Table(name = "offender")
data class Offender(
  @Id
  @Column("id")
  val id: UUID?,
  val crn: String?,
  val cro: String?,
  val pnc: String?,
  val isSuspendedSentenceOrder: Boolean?,
  val isBreach: Boolean?,
  val isAwaitingPSR: Boolean?,
  val probationStatus: String?,
  val isPreSentenceActivity: Boolean?,
  val previouslyKnownTerminationDate: LocalDate?,
  val createdAt: OffsetDateTime?,
  val createdBy: String?,
  val updatedAt: OffsetDateTime?,
  val updatedBy: String?,
  val isSoftDeleted: Boolean?,
  val version: Int?,
) : Serializable
