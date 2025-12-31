package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.common

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.time.OffsetDateTime
import java.util.UUID

@Table("failed_messages")
data class FailedMessages(
  @Id
  @Column("id")
  val id: UUID?,
  val messageId: UUID?,
  val correlationId: UUID?,
  val errorMessage: String?,
  val originalMessage: String?,
  val sourceType: String?,
  val createdAt: OffsetDateTime?,
  val createdBy: String?,
  val updatedAt: OffsetDateTime?,
  val updatedBy: String?,
  val isSoftDeleted: Boolean?,
  val version: Int?,
) : Serializable
