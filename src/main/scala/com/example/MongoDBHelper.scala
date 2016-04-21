package com.example

import com.mongodb.casbah.Imports._
import com.typesafe.config.ConfigFactory

class MongoDBHelper {
  val config = ConfigFactory.load()

  val mongoClient = MongoClient(config.getString("mongo.host"), config.getInt("mongo.port"))
  val db = mongoClient(config.getString("mongo.database"))

  def statisticsCollection = db(config.getString("mongo.statistics_collection"))
}
