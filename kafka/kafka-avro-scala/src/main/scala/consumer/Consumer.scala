package consumer

import java.util.Properties

import domain.Transaction
import org.apache.avro.Schema
import org.apache.avro.io.DatumReader
import org.apache.avro.io.Decoder
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.DecoderFactory
import kafka.consumer.{Consumer, ConsumerConfig, ConsumerTimeoutException, Whitelist}
import kafka.serializer.DefaultDecoder

import scala.io.Source


class KafkaConsumer(topic: String) {
  private val props = new Properties()

  val _topic = topic
  val groupId = topic + "-consumer" 

  props.put("group.id", groupId)
  props.put("zookeeper.connect", "localhost:2181")
  props.put("auto.offset.reset", "smallest")
  props.put("consumer.timeout.ms", "120000")
  props.put("auto.commit.interval.ms", "10000")

  private val consumerConfig = new ConsumerConfig(props)
  private val consumerConnector = Consumer.create(consumerConfig)
  private val filterSpec = new Whitelist(_topic)
  private val streams = consumerConnector.createMessageStreamsByFilter(filterSpec, 1, new DefaultDecoder(), new DefaultDecoder())(0)

  lazy val iterator = streams.iterator()

  val schemaString = Source.fromURL(getClass.getResource("/transaction_schema.avsc")).mkString
  // Initialize schema
  val schema: Schema = new Schema.Parser().parse(schemaString)

  private def getTran(message: Array[Byte]): Option[Transaction] = {

    // Deserialize and create generic record
    val reader: DatumReader[GenericRecord] = new SpecificDatumReader[GenericRecord](schema)
    val decoder: Decoder = DecoderFactory.get().binaryDecoder(message, null)
    val tranData: GenericRecord = reader.read(null, decoder)

    // Make user object
    // val tran = Transaction(tranData.get("tranId").toString, tranData.get("clientId").toString, tranData.get("pdType").toString, tranData.get("symbol").toString, tranData.get("unitNum").toString.toInt, try {
    val tran = Transaction(tranData.get("tranId").toString, tranData.get("tranTs").toString, tranData.get("clientId").toString, tranData.get("pdType").toString, tranData.get("symbol").toString, tranData.get("unitNum").toString.toInt, tranData.get("unitPrice").toString.toDouble)
    Some(tran)
  }

  /**
    * Read message from kafka queue
    *
    * @return Some of message if exist in kafka queue, otherwise None
    */
  def read() =
    try {
      if (hasNext) {
    //    println("Getting message from queue.............")
        val message: Array[Byte] = iterator.next().message()
        getTran(message)
      } else {
        None
      }
    } catch {
      case ex: Exception => ex.printStackTrace()
        None
    }

  private def hasNext: Boolean =
    try
      iterator.hasNext()
    catch {
      case timeOutEx: ConsumerTimeoutException =>
        false
      case ex: Exception => ex.printStackTrace()
        println("Got error when reading message ")
        false
    }

  def close(): Unit = consumerConnector.shutdown()

}
