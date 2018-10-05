package com.github.mnogu.gatling.kafka.action

import java.util.Date

import com.github.mnogu.gatling.kafka.protocol.KafkaProtocol
import com.github.mnogu.gatling.kafka.request.builder.{Avro4sAttributes, KafkaAttributes}
import io.gatling.commons.stats.{KO, OK}
import io.gatling.commons.util.ClockSingleton._
import io.gatling.commons.validation.Validation
import io.gatling.core.CoreComponents
import io.gatling.core.action.{Action, ExitableAction}
import io.gatling.core.session._
import io.gatling.core.stats.message.ResponseTimings
import io.gatling.core.util.NameGen
import org.apache.kafka.clients.producer._
import com.sksamuel.avro4s._
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream
import org.apache.avro.Schema
import org.apache.avro.Schema.Field
import org.apache.avro.generic.GenericRecord

import scala.util.{Failure, Success}

class KafkaAvro4sRequestAction[T](val producer: KafkaProducer[Nothing,GenericRecord],
                                  val avro4sAttributes: Avro4sAttributes[T],
                                      val coreComponents: CoreComponents,
                                      val kafkaProtocol: KafkaProtocol,
                                      val throttled: Boolean,
                                      val next: Action )
  extends ExitableAction with NameGen {

  // DateToValue, DateFromValue and DateToSchema are required to teach avro4s how
  // to serialize/deserialize a java.util.Date
  implicit object DateToValue extends ToValue[Date] {
    override def apply(value: Date): Long = {
      value.getTime
    }
  }

  implicit object DateFromValue extends FromValue[Date] {
    override def apply(value: Any, field: Field): Date = {
      new Date(value.asInstanceOf[Long])
    }
  }

  implicit object DateToSchema extends ToSchema[Date] {
    override val schema: Schema = Schema.create(Schema.Type.LONG)
  }

  implicit val schema = avro4sAttributes.schema
  implicit val fromRecord = avro4sAttributes.fromRecord

  val statsEngine = coreComponents.statsEngine
  override val name = genName("kafkaRequest")

  override def execute(session: Session): Unit = recover(session) {

    avro4sAttributes requestName session flatMap { requestName =>

      val outcome =
        sendRequest(
          requestName,
          producer,
          avro4sAttributes,
          throttled,
          session)

      outcome.onFailure(
        errorMessage =>
          statsEngine.reportUnbuildableRequest(session, requestName, errorMessage)
      )

      outcome

    }

  }

  private def sendRequest( requestName: String,
                           producer: Producer[Nothing,GenericRecord],
                           avro4sAttributes: Avro4sAttributes[T],
                           throttled: Boolean,
                           session: Session ): Validation[Unit] = {

    avro4sAttributes payload session map { payload =>

        val json = payload.asInstanceOf[String]

        logger.debug(s"sendRequest received json: ${json}")

        // payload should be a json string
        val in = new ByteInputStream(json.getBytes("UTF-8"), json.size)
        val input = AvroInputStream.json[T](in)
        input.singleEntity match {
          case Success(cassclass) =>  // should contain the case class of [T]
            val record = new ProducerRecord(kafkaProtocol.topic, avro4sAttributes.avroFormat.to(cassclass))

            val requestStartDate = nowMillis

            val x = producer.send(record, new Callback() {

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

                if (throttled) {
                  coreComponents.throttler.throttle(session.scenario, () => next ! session)
                } else {
                  next ! session
                }

              }
            })

          case Failure(ex) =>
            logger.error("Failure while converting JSON to case class:" + ex.getCause + ":" + ex.getMessage)
            throw ex
        }
    }

  }

}
