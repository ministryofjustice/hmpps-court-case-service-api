package uk.gov.justice.digital.hmpps.courtcaseserviceapi.listener

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.awspring.cloud.sqs.annotation.SqsListener
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.listener.ProbationOffenderDetailsEventService
import kotlin.jvm.java

@Slf4j
@Component
class ProbationOffenderDetailsEventListener
  (private val offenderService: ProbationOffenderDetailsEventService,
   private val objectMapper: ObjectMapper) {

  private companion object {
    val LOG: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @Throws(JsonProcessingException::class)
  @SqsListener(value = ["picprobationoffendereventsqueue"], factory = "hmppsQueueContainerFactoryProxy")
  fun processOffenderEventMessage(rawMessage: String): Mono<Void> {
    return Mono.just(rawMessage)
      .doOnNext { rawMessage -> LOG.info("Received offender event: {}", rawMessage) }
      .then();
  }
}