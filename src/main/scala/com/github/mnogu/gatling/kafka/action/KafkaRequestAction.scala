package com.github.mnogu.gatling.kafka.action

import java.util.Date

import akka.actor.ActorRef
import com.github.mnogu.gatling.kafka.config.KafkaProtocol
import io.gatling.core.action.{Failable, Interruptable}
import io.gatling.core.result.message.{KO, OK}
import io.gatling.core.result.writer.DataWriterClient
import io.gatling.core.session._
import io.gatling.core.util.TimeHelper.nowMillis
import io.gatling.core.validation.Validation
import org.apache.kafka.clients.producer._

import scala.collection.JavaConverters._
import scala.util.control.NonFatal

class KafkaRequestAction(
  val requestName: Expression[String],
  val kafkaProtocol: KafkaProtocol,
  val next: ActorRef)
  extends Interruptable with Failable with DataWriterClient {

  def executeOrFail(session: Session): Validation[Unit] =
    requestName(session).map { resolvedRequestName =>
      sendRequest(resolvedRequestName, session)
    }

  private def sendRequest(requestName: String, session: Session): Unit = {
    // send the request
    val producer = new KafkaProducer(kafkaProtocol.properties.asJava)

    val callback = new Callback() {
      def onCompletion(metadata: RecordMetadata, e: Exception): Unit = {
        if (e != null) {
          // log the outcome
          val now = nowMillis
          writeRequestData(
            session,
            requestName,
            now,
            now,
            now,
            now,
            KO,
            Some(e.toString))
        }
      }
    }

    // TODO: Don't hard-code payload
    val payload = new Date(nowMillis).toString.getBytes

    val record = new ProducerRecord(kafkaProtocol.topic, payload)

    // retrieve the response
    val requestStartDate = nowMillis
    val requestEndDate = nowMillis
    val future = producer.send(record)

    val responseStartDate = nowMillis
    try {
      future.get
    } catch {
      case NonFatal(exc) =>
        val responseEndDate = nowMillis
        producer.close()
        // log the outcome
        writeRequestData(
          session,
          requestName,
          requestStartDate,
          requestEndDate,
          responseStartDate,
          responseEndDate,
          KO,
          Some(exc.getMessage))

        next ! session
        return
    }
    val responseEndDate = nowMillis
    producer.close()

    // perform checks on the response (optional)

    // save elements from the checks into the session for further usage
    // (optional)

    // log the outcome
    writeRequestData(
      session,
      requestName,
      requestStartDate,
      requestEndDate,
      responseStartDate,
      responseEndDate,
      OK
    )

    next ! session
  }
}
