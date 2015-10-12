package com.github.mnogu.gatling.kafka.config

import io.gatling.core.config.Protocol

object KafkaProtocol {
  val DefaultKafkaProtocol = new KafkaProtocol(
    topic = "",
    properties = Map())
}

case class KafkaProtocol(
  topic: String,
  properties: Map[String, Object]) extends Protocol {

  def topic(topic: String): KafkaProtocol = copy(topic = topic)
  def properties(properties: Map[String, Object]): KafkaProtocol = copy(properties = properties)
}
