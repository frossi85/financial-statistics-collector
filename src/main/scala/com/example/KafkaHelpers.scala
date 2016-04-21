package com.example

import com.typesafe.config.ConfigFactory

class KafkaHelpers {
  val config = ConfigFactory.load()

  def kafkaSocket() :Socket = Socket(config.getString("kafka.host"), config.getInt("kafka.port"))
  def zookeeperSocket(): Socket = Socket(config.getString("zookeeper.host"), config.getInt("zookeeper.port"))

  def topic(): String = config.getString("kafka.topic")
  def groupId(): String = config.getString("kafka.groupId")
}

case class Socket(host: String, port: Int) {
  override def toString() = s"$host:$port"
}