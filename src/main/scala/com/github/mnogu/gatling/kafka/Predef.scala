package com.github.mnogu.gatling.kafka

import action.KafkaRequestActionBuilder
import com.github.mnogu.gatling.kafka.config.KafkaProtocol
import io.gatling.core.session.Expression

object Predef {
  def kafka = KafkaProtocol.DefaultKafkaProtocol

  def kafka(requestName: Expression[String]) =
    new KafkaRequestActionBuilder(requestName)
}