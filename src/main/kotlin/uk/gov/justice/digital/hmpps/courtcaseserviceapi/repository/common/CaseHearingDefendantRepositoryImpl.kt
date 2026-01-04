package uk.gov.justice.digital.hmpps.courtcaseserviceapi.repository.common

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.spi.Row
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.cases.CaseDocument
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.cases.CaseURN
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.common.caseDocuments.CaseHearingDefendant
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.defendant.Person
import java.util.UUID

@Repository
class CaseHearingDefendantRepositoryImpl(
  private val template: R2dbcEntityTemplate,
  private val objectMapper: ObjectMapper,
) : CaseHearingDefendantRepository {

  override fun findByHearingIdAndDefendantId(
    hearingId: UUID,
    defendantId: UUID,
  ): Mono<CaseHearingDefendant> {
    val sql = """
      SELECT
        h.hearing_id AS hearing_id,
        pc.id AS prosecution_case_id,
        pc.case_id AS case_id,
        pc.case_urn AS case_urn,
        pc.case_document AS case_document,
        d.defendant_id AS defendant_id,
        d.person AS person,
      FROM hearing h
      INNER JOIN defendant_hearing dh
        ON h.id = dh.hearing_id AND dh.is_soft_deleted = false
      INNER JOIN defendant d
        ON d.id = dh.defendant_id AND d.defendant_id = :defendantId AND d.is_soft_deleted = false
      INNER JOIN prosecution_case_hearing pch
        ON h.id = pch.hearing_id AND pch.is_soft_deleted = false
      INNER JOIN prosecution_case pc
        ON pc.id = pch.prosecution_case_id AND pc.is_soft_deleted = false
      INNER JOIN defendant_prosecution_case dpc
        ON dpc.prosecution_case_id = pc.id
        AND dpc.defendant_id = d.id
        AND dpc.is_soft_deleted = false
      WHERE h.hearing_id = :hearingId
        AND h.is_soft_deleted = false
    """

    return template.databaseClient.sql(sql)
      .bind("hearingId", hearingId)
      .bind("defendantId", defendantId)
      .map { row, _ -> mapToRow(row) }
      .one()
  }

  private fun mapToRow(row: Row): CaseHearingDefendant = CaseHearingDefendant(
    hearingId = row["hearing_id", UUID::class.java]
      ?: throw IllegalStateException("hearing_id cannot be null"),
    prosecutionCaseId = row["prosecution_case_id", UUID::class.java]
      ?: throw IllegalStateException("prosecution_case_id cannot be null"),
    caseId = row["case_id", String::class.java]
      ?: throw IllegalStateException("case_id cannot be null"),
    caseURN = row.readJson("case_urn", CASE_URN_TYPE),
    caseDocument = row.readJson("case_document", CASE_DOCUMENT_TYPE),
    defendantId = row["defendant_id", UUID::class.java]
      ?: throw IllegalStateException("defendant_id cannot be null"),
    person = row.readJson("person", PERSON_TYPE),
  )

  private fun <T> Row.readJson(name: String, typeReference: TypeReference<T>): T? {
    val json = get(name, String::class.java) ?: return null
    return objectMapper.readValue(json, typeReference)
  }

  private companion object {
    val CASE_URN_TYPE = object : TypeReference<List<CaseURN>>() {}
    val CASE_DOCUMENT_TYPE = object : TypeReference<List<CaseDocument>>() {}
    val PERSON_TYPE = object : TypeReference<List<Person>>() {}
  }
}
