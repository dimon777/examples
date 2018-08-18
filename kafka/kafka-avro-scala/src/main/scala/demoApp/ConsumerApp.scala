package demoApp

import consumer.KafkaConsumer

object ConsumerApp /*extends App*/ {

  def main(args: Array[String]) {
      if (args.length == 0) {
          println("Usage: demoApp [topic]")
          println("Usage: demoApp tran_topicX01")
          throw new IllegalArgumentException("Wrong number of arguments, exiting...");
      }
      val topic = args(0)
      val consumer = new KafkaConsumer(topic)

      while (true) {
          consumer.read() match {
              case Some(message) =>
                  println("Got message: " + message)
                  Thread.sleep(100)
              case _ =>
                  println("Queue is empty.......................  ")
                  // wait for 2 second
              Thread.sleep(2 * 1000)
          }
      }

   }
}
