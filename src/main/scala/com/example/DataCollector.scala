package com.example

import akka.actor.ActorSystem
import com.example.KafkaHelpers.Socket
import com.typesafe.scalalogging.LazyLogging
import scala.concurrent.Future

class DataCollector(
                     kafkaSocket: Socket,
                     zooKeeperSocket: Socket,
                     groupId: String,
                     topic: String
                   )(implicit system: ActorSystem) extends LazyLogging {
  val databaseDumpActor = system.actorOf(DatabaseDumpActor.props, "databaseDumpActor")

  import system.dispatcher

  def collect(): Unit = Future {
    // create consumer
    val consumer = new SimpleKafkaConsumer(kafkaSocket, zooKeeperSocket, groupId, topic)
    val iterator = consumer.read[List[StatisticData]](groupId)

    iterator foreach { data =>
      databaseDumpActor ! data
    }
  } recover {
    case exception => {
      logger.error("There was an error trying to collect data", exception)

      //Wait to hope that all are now working and collect again
      Thread.sleep(5000)
      this.collect()
    }
  }
}
