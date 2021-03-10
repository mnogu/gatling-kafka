package com.github.mnogu.gatling.kafka.request.builder

import com.github.mnogu.gatling.kafka.action.KafkaRequestActionBuilder
import io.gatling.core.session._

case class KafkaAttributes[K,V]( requestName: Expression[String],
                                 key: Option[Expression[K]],
                                 payload: Expression[V],
                                 topic: Option[Expression[String]])

case class KafkaRequestBuilder(requestName: Expression[String]) {

  def send[V](payload: Expression[V]): KafkaRequestActionBuilder[_,V] = send(payload, None, None)

  def send[K,V](key: Expression[K], payload: Expression[V]): KafkaRequestActionBuilder[K,V] = send(payload, Some(key), None)

  def send[K,V](topic: Expression[String], key: Expression[K], payload: Expression[V]): KafkaRequestActionBuilder[K,V] =
    send(payload, Some(key), Some(topic))

  private def send[K,V](payload: Expression[V], key: Option[Expression[K]], topic: Option[Expression[String]]) =
    new KafkaRequestActionBuilder(KafkaAttributes(requestName, key, payload, topic))

}
