package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.postgresql.codec.Json
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.defendant.Person
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.defendant.PersonWrapper

@WritingConverter
class PersonEncoder(
  private val objectMapper: ObjectMapper,
) : Converter<List<Person>, Json> {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: List<Person>): Json = try {
    log.info("Converting Person to JSON")
    val wrapper = PersonWrapper(source)
    val json = objectMapper.writeValueAsString(wrapper)
    Json.of(json)
  } catch (ex: JsonProcessingException) {
    throw IllegalArgumentException("Error converting Person list to JSON", ex)
  }
}

@ReadingConverter
class PersonDecoder(
  private val objectMapper: ObjectMapper,
) : Converter<Json, List<Person>> {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: Json): List<Person>? {
    log.info("Converting JSON to Person")
    try {
      val wrapper = objectMapper.readValue(
        source.asString(),
        PersonWrapper::class.java,
      )
      return wrapper.persons
    } catch (ex: JsonProcessingException) {
      throw IllegalArgumentException("Error converting JSON to Person list", ex)
    }
  }
}
