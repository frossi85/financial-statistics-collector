package com.example

import kafka.admin.TopicCommand
import kafka.server.KafkaConfig
import kafka.utils.{MockTime, TestUtils, TestZKUtils, ZKStringSerializer}
import kafka.zk.EmbeddedZookeeper
import org.I0Itec.zkclient.ZkClient

class KafkaTestServer {
  private val brokerId = 0

  // setup Zookeeper
  val zkConnect = TestZKUtils.zookeeperConnect
  val zkServer = new EmbeddedZookeeper(zkConnect)
  val zkClient = new ZkClient(zkServer.connectString, 30000, 30000, ZKStringSerializer)

  // setup Broker
  val host = "localhost"
  val port = TestUtils.choosePort()
  val props = TestUtils.createBrokerConfig(brokerId, port, true)

  val config = new KafkaConfig(props)
  val mock = new MockTime()
  val kafkaServer = TestUtils.createServer(config, mock);

  def shutdown() = {
    kafkaServer.shutdown()
    zkClient.close()
    zkServer.shutdown()
  }

  def createTopic(topicName: String) = {
    val arguments = Array("--topic", topicName, "--partitions", "1","--replication-factor", "1")
    // create topic
    TopicCommand.createTopic(zkClient, new TopicCommand.TopicCommandOptions(arguments))

    val servers = List(kafkaServer)
    TestUtils.waitUntilMetadataIsPropagated(servers, topicName, 0, 5000)
  }

  def cleanPreviousData(groupId: String) = {
    // deleting zookeeper information to make sure the consumer starts from the beginning
    // see https://stackoverflow.com/questions/14935755/how-to-get-data-from-old-offset-point-in-kafka
    zkClient.delete(s"/consumers/$groupId")
  }
}
