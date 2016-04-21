package com.example

import java.nio.charset.StandardCharsets

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.example.KafkaHelpers.Socket
import com.github.simplyscala.{MongoEmbedDatabase, MongodProps}
import de.flapdoodle.embed.mongo.distribution.Version
import kafka.producer.{KeyedMessage, Producer, ProducerConfig}
import kafka.utils.TestUtils
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Matchers, WordSpecLike}
import reactivemongo.api.{MongoConnectionOptions, MongoDriver}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class DataCollectorSpec(_system: ActorSystem) extends TestKit(_system)
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with ScalaFutures
  with BeforeAndAfterAll
  //with MongoEmbedDatabase
  //with BeforeAndAfter
 {

  /*var mongoProps: MongodProps = null

  before {
    mongoProps = mongoStart(27017, Version.V2_6_1)   // by default port = 12345 & version = Version.2.3.0
  }                               // add your own port & version parameters in mongoStart method if you need it

  after { mongoStop(mongoProps) }*/

  private val topic = "financial_statistics"
  private val groupId = "group0"
 
  def this() = this(ActorSystem("MySpec"))
 
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
 
  "The Crawler actor" must {
    "send financial statistics to kafka" in {

      //Initialize a Kafka Test Server
      val server = new KafkaTestServer
      server.cleanPreviousData(groupId)
      server.createTopic(topic)


      // setup producer
      val properties = TestUtils.getProducerConfig("localhost:" + server.port)
      val producerConfig = new ProducerConfig(properties)
      val producer = new Producer[Nothing, Array[Byte]](producerConfig)

      // send message
      val messageToSend = "{\"symbol\":\"YHOO\",\"Ask\":\"37.97\",\"AverageDailyVolume\":\"17621900\",\"Bid\":\"37.87\",\"AskRealtime\":null,\"BidRealtime\":null,\"BookValue\":\"30.78\",\"Change_PercentChange\":\"+1.51 - +4.16%\",\"Change\":\"+1.51\",\"Commission\":null,\"Currency\":\"USD\",\"ChangeRealtime\":null,\"AfterHoursChangeRealtime\":null,\"DividendShare\":null,\"LastTradeDate\":\"4/20/2016\",\"TradeDate\":null,\"EarningsShare\":\"-4.64\",\"ErrorIndicationreturnedforsymbolchangedinvalid\":null,\"EPSEstimateCurrentYear\":\"0.53\",\"EPSEstimateNextYear\":\"0.59\",\"EPSEstimateNextQuarter\":\"0.15\",\"DaysLow\":\"37.00\",\"DaysHigh\":\"38.19\",\"YearLow\":\"26.15\",\"YearHigh\":\"45.10\",\"HoldingsGainPercent\":null,\"AnnualizedGain\":null,\"HoldingsGain\":null,\"HoldingsGainPercentRealtime\":null,\"HoldingsGainRealtime\":null,\"MoreInfo\":null,\"OrderBookRealtime\":null,\"MarketCapitalization\":\"35.70B\",\"MarketCapRealtime\":null,\"EBITDA\":\"474.68M\",\"ChangeFromYearLow\":\"11.69\",\"PercentChangeFromYearLow\":\"+44.70%\",\"LastTradeRealtimeWithTime\":null,\"ChangePercentRealtime\":null,\"ChangeFromYearHigh\":\"-7.26\",\"PercebtChangeFromYearHigh\":\"-16.10%\",\"LastTradeWithTime\":\"4:00pm - <b>37.84</b>\",\"LastTradePriceOnly\":\"37.84\",\"HighLimit\":null,\"LowLimit\":null,\"DaysRange\":\"37.00 - 38.19\",\"DaysRangeRealtime\":null,\"FiftydayMovingAverage\":\"35.17\",\"TwoHundreddayMovingAverage\":\"32.81\",\"ChangeFromTwoHundreddayMovingAverage\":\"5.03\",\"PercentChangeFromTwoHundreddayMovingAverage\":\"+15.33%\",\"ChangeFromFiftydayMovingAverage\":\"2.67\",\"PercentChangeFromFiftydayMovingAverage\":\"+7.58%\",\"Name\":\"Yahoo! Inc.\",\"Notes\":null,\"Open\":\"37.03\",\"PreviousClose\":\"36.33\",\"PricePaid\":null,\"ChangeinPercent\":\"+4.16%\",\"PriceSales\":\"6.90\",\"PriceBook\":\"1.18\",\"ExDividendDate\":null,\"PERatio\":null,\"DividendPayDate\":null,\"PERatioRealtime\":null,\"PEGRatio\":\"112.38\",\"PriceEPSEstimateCurrentYear\":\"71.40\",\"PriceEPSEstimateNextYear\":\"64.14\",\"Symbol\":\"YHOO\",\"SharesOwned\":null,\"ShortRatio\":\"3.43\",\"LastTradeTime\":\"4:00pm\",\"TickerTrend\":null,\"OneyrTargetPrice\":\"38.85\",\"Volume\":\"30761642\",\"HoldingsValue\":null,\"HoldingsValueRealtime\":null,\"YearRange\":\"26.15 - 45.10\",\"DaysValueChange\":null,\"DaysValueChangeRealtime\":null,\"StockExchange\":\"NMS\",\"DividendYield\":null,\"PercentChange\":\"+4.16%\"}"
      val data = new KeyedMessage[Nothing, Array[Byte]](topic, messageToSend.getBytes(StandardCharsets.UTF_8))
      producer.send(data)
      producer.close()


      val zkSocketparts = server.zkConnect.split(':')

      //Collect the messages from Kafka
      val dataCollector = new DataCollector(
        Socket(server.host, server.port),
        Socket(zkSocketparts(0), zkSocketparts(1).toInt),
        groupId,
        topic
      )

      //Make it non blocking
      Future(dataCollector.collect())


      Thread.sleep(500)


      // gets an instance of the driver
      // (creates an actor system)
      val driver = new MongoDriver
      val connection = driver.connection(List("localhost"))

      // Gets a reference to the database "plugin"
      val db = connection("test")

      // Gets a reference to the collection "acoll"
      // By default, you get a BSONCollection.
      val collection = db[BSONCollection]("acoll")

      val a = BSONDocument("symbol" -> "YHOO")
      Await.result(collection.insert(a), Duration.Inf)


      val query = BSONDocument("symbol" -> "YHOO")
      val futureList: Future[List[BSONDocument]] = collection.find(query).cursor[BSONDocument].collect[List]()

      Await.result(futureList, Duration.Inf)


      whenReady(futureList) { list =>
        list.length shouldEqual 1
      }
    }
  }
}
