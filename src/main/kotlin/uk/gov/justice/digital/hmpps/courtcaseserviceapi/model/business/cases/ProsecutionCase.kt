package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.cases

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.time.OffsetDateTime
import java.util.UUID

@Table(name = "prosecution_case")
data class ProsecutionCase(
  @Id
  @Column("id")
  val id: UUID?,
  val caseURN: CaseURN? = null,
  val sourceType: String?,
  val caseMarker: CaseMarker? = null,
  val caseDocument: CaseDocument? = null,
  val createdAt: OffsetDateTime?,
  val createdBy: String?,
  val updatedAt: OffsetDateTime?,
  val updatedBy: String?,
  val isSoftDeleted: Boolean?,
  val version: Int?,
) : Serializable
