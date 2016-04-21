package com.example
import java.nio.charset.StandardCharsets

import kafka.consumer.ConsumerConfig
import kafka.producer.{KeyedMessage, Producer, ProducerConfig}
import kafka.utils.TestUtils
import org.scalatest.{FunSpec, Matchers}
//import org.junit.Assert._
import scala.collection.immutable.HashMap

/*
class SimpleKafkaConsumerTest extends FunSpec with Matchers{

  private val topic = "test"
  private val groupId = "group0"

  case class MessageData(a: String, b: String)

  describe("The SimpleKafka Api") {
    it("Should receive data using the consumer") {
      // setup producer
      val properties = TestUtils.getProducerConfig("localhost:" + KafkaHelpers.kafkaSocket().port)
      val producerConfig = new ProducerConfig(properties)
      val producer = new Producer[Nothing, Array[Byte]](producerConfig)

      // send message
      val messageToSend = "{\"a\":\"Hello\",\"b\":\"World\"}"
      val data = new KeyedMessage[Nothing, Array[Byte]](topic, messageToSend.getBytes(StandardCharsets.UTF_8))

      //Sends data
      producer.send(data)
      producer.close()

      // create consumer
      val consumer = new SimpleKafkaConsumer(KafkaHelpers.kafkaSocket(), KafkaHelpers.zookeeperSocket(), groupId, topic)
      val iterator = consumer.read[MessageData]()


      if(!iterator.isEmpty) {
        val msg = iterator.head

        assert("Hello" == msg.a)
        assert("World" == msg.b)
      } else {
        fail()
      }
    }
  }
}
*/
