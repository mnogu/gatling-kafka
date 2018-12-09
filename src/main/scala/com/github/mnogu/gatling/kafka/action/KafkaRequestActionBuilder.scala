package com.github.mnogu.gatling.kafka.action

import com.github.mnogu.gatling.kafka.protocol.{KafkaComponents, KafkaProtocol}
import com.github.mnogu.gatling.kafka.request.builder.KafkaAttributes
import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioContext
import org.apache.kafka.clients.producer.KafkaProducer

import scala.collection.JavaConverters._


class KafkaRequestActionBuilder[K,V](kafkaAttributes: KafkaAttributes[K,V]) extends ActionBuilder {

  override def build( ctx: ScenarioContext, next: Action ): Action = {
    import ctx.{protocolComponentsRegistry, coreComponents, throttled}

    val kafkaComponents: KafkaComponents = protocolComponentsRegistry.components(KafkaProtocol.KafkaProtocolKey)

    val producer = new KafkaProducer[K,V]( kafkaComponents.kafkaProtocol.properties.asJava )

    coreComponents.actorSystem.registerOnTermination(producer.close())

    new KafkaRequestAction(
      producer,
      kafkaAttributes,
      coreComponents,
      kafkaComponents.kafkaProtocol,
      throttled,
      next
    )

  }

}