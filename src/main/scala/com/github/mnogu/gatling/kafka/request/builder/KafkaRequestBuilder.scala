package com.github.mnogu.gatling.kafka.request.builder

import com.github.mnogu.gatling.kafka.action.{KafkaAvro4sActionBuilder, KafkaRequestActionBuilder}
import com.sksamuel.avro4s.{FromRecord, RecordFormat, SchemaFor}
import io.gatling.core.session._

case class KafkaAttributes[K,V]( requestName: Expression[String],
                                 key: Option[Expression[K]],
                                 payload: Expression[V] )

case class Avro4sAttributes[T]( requestName: Expression[String],
                                schema: SchemaFor[T],
                                avroFormat: RecordFormat[T],
                                fromRecord: FromRecord[T],
                                payload: Expression[String]
                              )

case class KafkaRequestBuilder(requestName: Expression[String]) {

  def send[V](payload: Expression[V]): KafkaRequestActionBuilder[_,V] = send(payload, None)

  def send[K,V](key: Expression[K], payload: Expression[V]): KafkaRequestActionBuilder[K,V] = send(payload, Some(key))

  private def send[K,V](payload: Expression[V], key: Option[Expression[K]]) =
    new KafkaRequestActionBuilder(KafkaAttributes(requestName, key, payload))

  def sendAvro[T](schema: SchemaFor[T], format: RecordFormat[T], fromRecord: FromRecord[T], payload: Expression[String]): KafkaAvro4sActionBuilder[T] = new KafkaAvro4sActionBuilder(Avro4sAttributes(requestName, schema, format, fromRecord, payload))

}
