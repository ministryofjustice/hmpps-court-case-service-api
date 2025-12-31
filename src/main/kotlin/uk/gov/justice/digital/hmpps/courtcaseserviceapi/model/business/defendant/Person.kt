package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.defendant

import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.common.Address
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

data class Person(
  val id: UUID,
  val title: String?,
  val firstName: String?,
  val middleName: String?,
  val lastName: String?,
  val dateOfBirth: LocalDate?,
  val nationality: Nationality?,
  val disabilityStatus: String?,
  val sex: Sex?,
  val nationalInsuranceNumber: String?,
  val occupation: String?,
  val occupationCode: String?,
  val contactInformation: ContactInformation?,
  val address: Address?,
  val createdAt: OffsetDateTime?,
  val createdBy: String?,
  val updatedAt: OffsetDateTime?,
  val updatedBy: String?,
  val isSoftDeleted: Boolean?,
  val version: Int?,
)
