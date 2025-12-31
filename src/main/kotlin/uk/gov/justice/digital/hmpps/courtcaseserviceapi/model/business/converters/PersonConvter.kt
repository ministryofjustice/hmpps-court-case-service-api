package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters

import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.postgresql.codec.Json
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.defendant.Person

@WritingConverter
class PersonEncoder : Converter<Person, Json> {
  private val objMapper = ObjectMapper()

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: Person): Json {
    log.info("Converting Person to JSON")
    return Json.of(objMapper.writeValueAsString(source))
  }
}

@ReadingConverter
class PersonDecoder : Converter<Json, Person> {
  private val objectMapper = ObjectMapper()

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: Json): Person {
    log.info("Converting JSON to Person")
    return objectMapper.readValue(source.asString(), Person::class.java)
  }
}
