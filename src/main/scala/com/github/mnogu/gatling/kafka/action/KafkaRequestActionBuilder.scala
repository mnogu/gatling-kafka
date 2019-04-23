package com.github.mnogu.gatling.kafka.action

import com.github.mnogu.gatling.kafka.protocol.{KafkaComponents, KafkaProtocol}
import com.github.mnogu.gatling.kafka.request.builder.KafkaAttributes
import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioContext
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.Serializer

import scala.collection.JavaConverters._


class KafkaRequestActionBuilder[K, V](kafkaAttributes: KafkaAttributes[K, V]) extends ActionBuilder {

  override def build( ctx: ScenarioContext, next: Action ): Action = {
    import ctx.{protocolComponentsRegistry, coreComponents, throttled}

    val kafkaComponents: KafkaComponents = protocolComponentsRegistry.components(KafkaProtocol.KafkaProtocolKey)
    val producer = new KafkaProducer[K, V](
      kafkaComponents.kafkaProtocol.properties.asJava,
      kafkaComponents.kafkaProtocol.keySerializerOpt.asInstanceOf[Option[Serializer[K]]].orNull,
      kafkaComponents.kafkaProtocol.valueSerializerOpt.asInstanceOf[Option[Serializer[V]]].orNull
    )

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