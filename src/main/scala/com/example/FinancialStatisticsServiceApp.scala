package com.example

import akka.actor.ActorSystem
import com.typesafe.scalalogging.LazyLogging
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object FinancialStatisticsServiceApp extends App with LazyLogging {
  implicit val system = ActorSystem("MyActorSystem")

  val kafkaHelpers = new KafkaHelpers()

  val dataCollector = new DataCollector(
    kafkaHelpers.kafkaSocket(),
    kafkaHelpers.zookeeperSocket(),
    kafkaHelpers.groupId(),
    kafkaHelpers.topic()
  )

  dataCollector.collect() recover {
    case exception => {
      logger.error("There was an error trying to collect data", exception)

      //Wait to hope that all are now working and collect again
      Thread.sleep(5000)
      dataCollector.collect()
    }
  }

  Await.result(system.whenTerminated, Duration.Inf)
}


