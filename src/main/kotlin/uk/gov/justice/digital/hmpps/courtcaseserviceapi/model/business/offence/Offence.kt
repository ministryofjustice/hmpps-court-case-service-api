package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.offence

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.time.OffsetDateTime
import java.util.UUID

@Table(name = "offence")
data class Offence(
  @Id
  @Column("id")
  val id: UUID,
  val code: String?,
  val title: String?,
  val legislation: String?,
  val listingNumber: Int?,
  val wording: String?,
  val sequence: Int?,
  val shortTermCustodyPredictorScore: Float?,
  val verdict: Verdict? = null,
  val plea: Plea? = null,
  val judicialResult: JudicialResult? = null,
  val createdAt: OffsetDateTime?,
  val createdBy: String?,
  val updatedAt: OffsetDateTime?,
  val updatedBy: String?,
  val isSoftDeleted: Boolean?,
  val version: Int?,
) : Serializable
