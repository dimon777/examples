package demoApp

import domain.User
import domain.Transaction
import producer.KafkaProducer
import java.time.LocalDateTime

object ProducerApp {

  val producer = new KafkaProducer()

  def main(args: Array[String]) {
      if (args.length != 6) {
          println("Usage: demoApp [topic] [clientId] [pdType] [symbol] [units] [unitPrice]")
          println("Usage: demoApp t_topic Bob equity IBM 100.10 10")
          throw new IllegalArgumentException("Wrong number of arguments, exiting...");
      }
      val topic = args(0)
      val clientId = args(1)
      val pdType = args(2)
      val symbol = args(3)
      val unitNum = args(4).toInt
      val unitPrice = args(5).toDouble
      val tranId = java.util.UUID.randomUUID().toString
      val tranTs = LocalDateTime.now().toString;
      val t = Transaction(tranId, tranTs, clientId, pdType, symbol, unitNum, unitPrice)
      println("unitPrice: "+unitPrice)
      producer.send(topic, List(t))
    
  }

}
