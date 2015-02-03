package com.github.mnogu.gatling.kafka.request.builder

import com.github.mnogu.gatling.kafka.action.KafkaRequestActionBuilder
import io.gatling.core.session._

case class KafkaAttributes(
  requestName: Expression[String],
  key: Option[Expression[String]],
  payload: Expression[String])

case class KafkaRequestBuilder(requestName: Expression[String]) {
  def send(payload: Expression[String]): KafkaRequestActionBuilder =
    send(payload, None)
  def send(key: Expression[String], payload: Expression[String]): KafkaRequestActionBuilder =
    send(payload, Some(key))

  private def send(payload: Expression[String], key: Option[Expression[String]]) =
    new KafkaRequestActionBuilder(KafkaAttributes(requestName, key, payload))
}
