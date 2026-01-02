package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.court

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
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

  @Column("legacy_id")
  val legacyId: Int?,

  @Column("code")
  val code: String?,

  @Column("name")
  val name: String?,

  @Column("court_room")
  val courtRoom: List<CourtRoom>? = null,

  @Column("psa_code")
  val psaCode: String?,

  @Column("region")
  val region: String?,

  @Column("address")
  val address: List<Address>? = null,

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
