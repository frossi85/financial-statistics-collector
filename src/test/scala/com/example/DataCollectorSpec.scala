package com.example

import java.nio.charset.StandardCharsets
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.mongodb.casbah.commons.MongoDBObject
import kafka.producer.{KeyedMessage, Producer, ProducerConfig}
import kafka.utils.TestUtils
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataCollectorSpec(_system: ActorSystem) extends TestKit(_system)
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with ScalaFutures
  with BeforeAndAfterAll
 {
  def this() = this(ActorSystem("MySpec"))
 
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
 
  "The DataCollector" must {
    "collect financial statistics from kafka and save it to mongo" in {
      val kafkaHelpers = new KafkaHelpers()
      val kafkaSocket = kafkaHelpers.kafkaSocket()
      val zooKeeperSocket = kafkaHelpers.zookeeperSocket()

      // setup producer
      val properties = TestUtils.getProducerConfig("localhost:" + kafkaSocket.port)
      val producerConfig = new ProducerConfig(properties)
      val producer = new Producer[Nothing, Array[Byte]](producerConfig)

      // send message
      val messageToSend: String = "{\"symbol\":\"YHOO\",\"Ask\":\"37.97\",\"AverageDailyVolume\":\"17621900\",\"Bid\":\"37.87\",\"AskRealtime\":null,\"BidRealtime\":null,\"BookValue\":\"30.78\",\"Change_PercentChange\":\"+1.51 - +4.16%\",\"Change\":\"+1.51\",\"Commission\":null,\"Currency\":\"USD\",\"ChangeRealtime\":null,\"AfterHoursChangeRealtime\":null,\"DividendShare\":null,\"LastTradeDate\":\"4/20/2016\",\"TradeDate\":null,\"EarningsShare\":\"-4.64\",\"ErrorIndicationreturnedforsymbolchangedinvalid\":null,\"EPSEstimateCurrentYear\":\"0.53\",\"EPSEstimateNextYear\":\"0.59\",\"EPSEstimateNextQuarter\":\"0.15\",\"DaysLow\":\"37.00\",\"DaysHigh\":\"38.19\",\"YearLow\":\"26.15\",\"YearHigh\":\"45.10\",\"HoldingsGainPercent\":null,\"AnnualizedGain\":null,\"HoldingsGain\":null,\"HoldingsGainPercentRealtime\":null,\"HoldingsGainRealtime\":null,\"MoreInfo\":null,\"OrderBookRealtime\":null,\"MarketCapitalization\":\"35.70B\",\"MarketCapRealtime\":null,\"EBITDA\":\"474.68M\",\"ChangeFromYearLow\":\"11.69\",\"PercentChangeFromYearLow\":\"+44.70%\",\"LastTradeRealtimeWithTime\":null,\"ChangePercentRealtime\":null,\"ChangeFromYearHigh\":\"-7.26\",\"PercebtChangeFromYearHigh\":\"-16.10%\",\"LastTradeWithTime\":\"4:00pm - <b>37.84</b>\",\"LastTradePriceOnly\":\"37.84\",\"HighLimit\":null,\"LowLimit\":null,\"DaysRange\":\"37.00 - 38.19\",\"DaysRangeRealtime\":null,\"FiftydayMovingAverage\":\"35.17\",\"TwoHundreddayMovingAverage\":\"32.81\",\"ChangeFromTwoHundreddayMovingAverage\":\"5.03\",\"PercentChangeFromTwoHundreddayMovingAverage\":\"+15.33%\",\"ChangeFromFiftydayMovingAverage\":\"2.67\",\"PercentChangeFromFiftydayMovingAverage\":\"+7.58%\",\"Name\":\"Yahoo! Inc.\",\"Notes\":null,\"Open\":\"37.03\",\"PreviousClose\":\"36.33\",\"PricePaid\":null,\"ChangeinPercent\":\"+4.16%\",\"PriceSales\":\"6.90\",\"PriceBook\":\"1.18\",\"ExDividendDate\":null,\"PERatio\":null,\"DividendPayDate\":null,\"PERatioRealtime\":null,\"PEGRatio\":\"112.38\",\"PriceEPSEstimateCurrentYear\":\"71.40\",\"PriceEPSEstimateNextYear\":\"64.14\",\"Symbol\":\"YHOO\",\"SharesOwned\":null,\"ShortRatio\":\"3.43\",\"LastTradeTime\":\"4:00pm\",\"TickerTrend\":null,\"OneyrTargetPrice\":\"38.85\",\"Volume\":\"30761642\",\"HoldingsValue\":null,\"HoldingsValueRealtime\":null,\"YearRange\":\"26.15 - 45.10\",\"DaysValueChange\":null,\"DaysValueChangeRealtime\":null,\"StockExchange\":\"NMS\",\"DividendYield\":null,\"PercentChange\":\"+4.16%\"}"
      val data = new KeyedMessage[Nothing, Array[Byte]](kafkaHelpers.topic(), messageToSend.getBytes(StandardCharsets.UTF_8))
      producer.send(data)
      producer.close()

      //Collect the messages from Kafka
      val dataCollector = new DataCollector(
        kafkaSocket,
        zooKeeperSocket,
        kafkaHelpers.groupId(),
        kafkaHelpers.topic()
      )

      //Make it non blocking
      dataCollector.collect()

      //Wait until some data be collected
      Thread.sleep(1000)

      val allDocs = new MongoDBHelper().statisticsCollection.find(MongoDBObject("symbol" -> "YHOO"))

      allDocs.size >= 1
    }
  }
}
