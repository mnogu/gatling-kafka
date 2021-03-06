package com.github.mnogu.gatling.kafka.protocol

import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.session.Session


case class KafkaComponents(kafkaProtocol: KafkaProtocol) extends ProtocolComponents {

  override def onStart: Session => Session = Session.Identity

  override def onExit: Session => Unit = ProtocolComponents.NoopOnExit

}
