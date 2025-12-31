package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.court

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.common.Address
import java.io.Serializable
import java.time.OffsetDateTime
import java.util.UUID

@Table("court_centre")
data class CourtCentre(
  @Id
  @Column("id")
  val id: UUID?,
  val centreId: UUID?,
  val code: String?,
  val name: String?,
  val courtRoom: CourtRoom? = null,
  val psaCode: String?,
  val region: String?,
  val address: Address?,
  val createdAt: OffsetDateTime?,
  val createdBy: String?,
  val updatedAt: OffsetDateTime?,
  val updatedBy: String?,
  val isSoftDeleted: Boolean?,
  val version: Int?,
) : Serializable
