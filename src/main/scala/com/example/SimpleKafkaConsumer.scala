package com.example

import java.nio.charset.StandardCharsets
import java.util.Properties
import kafka.consumer.ConsumerConfig
import org.json4s.{DefaultFormats, jackson}
import scala.collection.immutable.HashMap

class SimpleKafkaConsumer(kafkaSocket: Socket, zooKeeperSocket: Socket, groupId: String, topic: String) {

  private def configuration = {
    val deserializer = "org.apache.kafka.common.serialization.StringDeserializer"
    val props = new Properties()
    props.put("bootstrap.servers", kafkaSocket.toString())
    props.put("key.deserializer", deserializer)
    props.put("value.deserializer", deserializer)
    props.put("group.id", groupId)
    props.put("consumer.id", "consumer0")
    props.put("consumer.timeout", "-1")
    props.put("auto.offset.reset", "smallest")
    props.put("zookeeper.sync.time.ms", "200")
    props.put("zookeeper.session.timeout.ms", "6000")
    props.put("zookeeper.connect", zooKeeperSocket.toString())
    props.put("num.consumer.fetchers", "2")
    props.put("rebalance.max.retries", "4")
    props.put("auto.commit.interval.ms", "1000")
    props
  }

  private val consumer = kafka.consumer.Consumer.create(new ConsumerConfig(configuration))

  def read[T <: AnyRef]()(implicit m: Manifest[T]): Iterable[T] = {
    implicit val serialization = jackson.Serialization
    implicit val formats = DefaultFormats

    val topicCountMap = HashMap(topic -> 1)
    val consumerMap = consumer.createMessageStreams(topicCountMap)
    val stream = consumerMap.get(topic).get(0)
    val iterator = stream.iterator()

    iterator.map(x => serialization.read[T](new String(x.message(), StandardCharsets.UTF_8))).toStream
  }

  def shutdown() = {
    consumer.shutdown()
  }
}



