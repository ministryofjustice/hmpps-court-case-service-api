package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.util.UUID

@Table(name = "hearing_day")
data class HearingDay(
  @Id
  @Column("id")
  val id: UUID,
  val courtCentreId: UUID?,
  val hearingId: UUID?,
  val sittingDay: LocalDate?,
  val hearingDayTime: LocalTime?,
  val listedDurationMinutes: LocalTime?,
  val isCancelled: Boolean?,
  val createdAt: OffsetDateTime?,
  val createdBy: String?,
  val updatedAt: OffsetDateTime?,
  val updatedBy: String?,
  val isSoftDeleted: Boolean?,
  val version: Int?,
) : Serializable
