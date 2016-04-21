package com.example

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import scala.concurrent.Await
import scala.concurrent.duration._

object FinancialStatisticsServiceApp extends App {
  implicit val system = ActorSystem("MyActorSystem")

  val config = ConfigFactory.load()

  val dataCollector = new DataCollector(
    KafkaHelpers.kafkaSocket(),
    KafkaHelpers.zookeeperSocket(),
    KafkaHelpers.groupId(),
    KafkaHelpers.topic()
  )

  dataCollector.collect()

  Await.result(system.whenTerminated, Duration.Inf)
}


