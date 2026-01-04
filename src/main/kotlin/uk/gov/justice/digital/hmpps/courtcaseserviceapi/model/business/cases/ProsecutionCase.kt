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
  val caseId: String?,
  val caseNumber: String?,
  @Column("case_urn")
  val caseURN: List<CaseURN>? = null,
  @Column("source_type")
  val sourceType: String?,
  @Column("case_marker")
  val caseMarker: List<CaseMarker>? = null,
  @Column("case_document")
  val caseDocument: List<CaseDocument>? = null,
  val createdAt: OffsetDateTime?,
  val createdBy: String?,
  val updatedAt: OffsetDateTime?,
  val updatedBy: String?,
  val isSoftDeleted: Boolean?,
  val version: Int?,
) : Serializable
