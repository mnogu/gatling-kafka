package com.github.mnogu.gatling.kafka.action

import com.github.mnogu.gatling.kafka.protocol.KafkaProtocol
import com.github.mnogu.gatling.kafka.request.builder.KafkaAttributes
import io.gatling.core.action.{Action, ExitableAction}
import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.session._
import io.gatling.commons.util.ClockSingleton._
import io.gatling.commons.validation.Validation
import io.gatling.core.CoreComponents
import io.gatling.core.util.NameGen
import io.gatling.core.stats.message.ResponseTimings
import org.apache.kafka.clients.producer._



class KafkaRequestAction[K,V]( val producer: KafkaProducer[K,V],
                               val kafkaAttributes: KafkaAttributes[K,V],
                               val coreComponents: CoreComponents,
                               val kafkaProtocol: KafkaProtocol,
                               val next: Action )
  extends ExitableAction with NameGen {

  val statsEngine = coreComponents.statsEngine

  override val name = genName("kafkaRequest")

  override def execute(session: Session): Unit = recover(session) {

    kafkaAttributes.requestName(session).flatMap { resolvedRequestName =>
      val payload = kafkaAttributes.payload

      val outcome = kafkaAttributes.key match {
        case Some(k) => k(session).flatMap { resolvedKey =>
          sendRequest(
            resolvedRequestName,
            producer,
            Some(resolvedKey),
            payload,
            session )
        }
        case None =>
          sendRequest(
            resolvedRequestName,
            producer,
            None,
            payload,
            session )
      }

      outcome.onFailure(
        errorMessage =>
          statsEngine.reportUnbuildableRequest(session, resolvedRequestName, errorMessage)
      )

      outcome

    }

  }

  private def sendRequest( requestName: String,
                           producer: Producer[K,V],
                           key: Option[K],
                           payload: Expression[V],
                           session: Session ): Validation[Unit] = {

    payload(session).map { resolvedPayload =>
      val record = key match {
        case Some(k) => new ProducerRecord[K,V](kafkaProtocol.topic, k, resolvedPayload)
        case None => new ProducerRecord[K,V](kafkaProtocol.topic, resolvedPayload)
      }

      val requestStartDate = nowMillis

      producer.send(record, new Callback() {

        override def onCompletion(m: RecordMetadata, e: Exception): Unit = {

          val requestEndDate = nowMillis
          statsEngine.logResponse(
            session,
            requestName,
            ResponseTimings(startTimestamp = requestStartDate, endTimestamp = requestEndDate),
            if (e == null) OK else KO,
            None,
            if (e == null) None else Some(e.getMessage)
          )

          next ! session

        }

      })

    }
  }

}
