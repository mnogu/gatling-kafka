package com.github.mnogu.gatling.kafka.request.builder

import java.util

import com.github.mnogu.gatling.kafka.action.KafkaRequestActionBuilder
import io.gatling.core.session.Expression
import org.apache.kafka.common.header.Header

case class KafkaAttributes[K,V]( requestName: Expression[String],
                                 key: Option[Expression[K]],
                                 payload: Expression[V],
                                 headers: util.List[Header]
                               )

case class KafkaRequestBuilder(requestName: Expression[String]) {

  def send[V](payload: Expression[V]): KafkaRequestActionBuilder[_,V] = send(payload, None,null)

  def send[K,V](key: Expression[K], payload: Expression[V]): KafkaRequestActionBuilder[K,V] = send(payload, Some(key), null)

  def send[K,V](key: Expression[K], payload: Expression[V], headers:util.List[Header] ): KafkaRequestActionBuilder[K,V] = send(payload, Some(key), headers)

  private def send[K,V](payload: Expression[V], key: Option[Expression[K]],headers: util.List[Header] ) =
    new KafkaRequestActionBuilder(KafkaAttributes(requestName, key, payload,headers))

}
