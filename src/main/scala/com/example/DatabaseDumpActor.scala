package com.example

import akka.actor.{Actor, ActorLogging, Props}

class DatabaseDumpActor extends Actor with ActorLogging with AutoMarshaller {
  def receive = {
  	case statistic: StatisticData =>
      new MongoDBHelper().statisticsCollection.insert(statistic.toMongoDBObject)
  }
}

object DatabaseDumpActor {
  val props = Props[DatabaseDumpActor]
}







