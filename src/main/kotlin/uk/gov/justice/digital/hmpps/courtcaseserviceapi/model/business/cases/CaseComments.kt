package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.cases

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.time.OffsetDateTime
import java.util.UUID

@Table("case_comments")
data class CaseComments(
  @Id
  @Column("id")
  val id: UUID?,
  val defendantID: UUID?,
  val caseID: UUID?,
  val author: String?,
  val comment: String?,
  val isDraft: Boolean?,
  val isLegacy: Boolean?,
  val createdAt: OffsetDateTime?,
  val createdBy: String?,
  val updatedAt: OffsetDateTime?,
  val updatedBy: String?,
  val isSoftDeleted: Boolean?,
  val version: Int?,
) : Serializable
