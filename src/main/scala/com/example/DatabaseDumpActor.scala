package com.example

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.typesafe.config.ConfigFactory
import reactivemongo.api._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument

class DatabaseDumpActor extends Actor with ActorLogging with AutoMarshaller {
  import DatabaseDumpActor._
  import context.dispatcher

  implicit val system: ActorSystem = context.system
  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(system))

  def receive = {
  	case Data(data) =>
      dumpToDatabase(data.map(x => x.toBSONDocument))
  }

  def dumpToDatabase(data: List[BSONDocument]) = {
    // gets an instance of the driver
    // (creates an actor system)
    val driver = new MongoDriver
    val connection = driver.connection(List(MongoDBHelper.server()))

    // Gets a reference to the database "plugin"
    val db = connection(MongoDBHelper.database())
    val collection = db[BSONCollection](MongoDBHelper.collection())

    data.map(x => collection.insert(x))
  }
}

object DatabaseDumpActor {
  val props = Props[DatabaseDumpActor]
  case class Data(data: List[StatisticData])
}


object MongoDBHelper {
  val config = ConfigFactory.load()

  def server() = config.getString("mongo.server")
  def database() = config.getString("mongo.database")
  def collection() = "collection"
}



