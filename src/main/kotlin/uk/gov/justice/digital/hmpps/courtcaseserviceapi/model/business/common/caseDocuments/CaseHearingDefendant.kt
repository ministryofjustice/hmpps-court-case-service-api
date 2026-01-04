package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.common.caseDocuments

import org.springframework.data.relational.core.mapping.Column
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.cases.CaseDocument
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.cases.CaseURN
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.defendant.Person
import java.util.UUID

data class CaseHearingDefendant(
  @Column("hearing_id")
  val hearingId: UUID,
  @Column("prosecution_case_id")
  val prosecutionCaseId: UUID,
  @Column("case_number")
  val caseId: String,
  @Column("case_urn")
  val caseURN: List<CaseURN>?,
  @Column("case_document")
  val caseDocument: List<CaseDocument>?,
  @Column("defendant_id")
  val defendantId: UUID,
  @Column("person")
  val person: List<Person>?,
)
