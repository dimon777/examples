package app

//import domain.User
//import domain.Transaction
//import producer.KafkaProducer

/*
1. Read file from directory
2. Calculate SHA256
3. Verify if file is already in the DB if not:
4. Parse pdf to test
5. Insert into DB
6. If succesful move file to processed directory


DROP TABLE pdf_documents;
CREATE TABLE `pdf_documents` (
  `hash` varchar(64) NOT NULL,
  `filename` varchar(128) NOT NULL,
  `parse_dt` DATETIME NOT NULL,
  `page_count` INTEGER DEFAULT NULL,
  `title` varchar(256) DEFAULT NULL,
  `author` varchar(256) DEFAULT NULL,
  `subject` varchar(256) DEFAULT NULL,
  `keywords` varchar(256) DEFAULT NULL,
  `creator`   varchar(256) DEFAULT NULL,
  `producer` varchar(256) DEFAULT NULL,
  `created_dt` DATETIME DEFAULT NULL,
  `content` MEDIUMTEXT DEFAULT NULL
) ENGINE=InnoDB;

CREATE INDEX pdf_documents_ix1 on pdf_documents(hash);

*/
import java.time.LocalDateTime
import java.sql.Timestamp
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.TimeZone
import java.security.{MessageDigest, DigestInputStream}
import java.io.{File, FileInputStream}
import java.sql.DriverManager
import java.sql.Connection
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.{Files, Path, StandardCopyOption}
import java.time.LocalDateTime
import org.apache.tika.parser.pdf.PDFParser
import org.apache.tika.sax.BodyContentHandler
import org.apache.tika.exception.TikaException
import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.AutoDetectParser
import org.apache.tika.parser.ParseContext
import org.apache.tika.parser.Parser
import org.apache.tika.parser.ocr.TesseractOCRConfig
import org.apache.tika.parser.pdf.PDFParserConfig
import org.apache.tika.sax.BodyContentHandler
import org.xml.sax.SAXException
/*
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.util.PDFTextStripper
*/
import domain.PdfRecord

object PdftodbApp {

//  var c: Connection

  def computeHash(path: String): String = {
    val buffer = new Array[Byte](8192)
    val md5 = MessageDigest.getInstance("MD5")
    val dis = new DigestInputStream(new FileInputStream(new File(path)), md5)
    try { 
      while (dis.read(buffer) != -1) { } 
    } finally { 
      dis.close() 
    }
    md5.digest.map("%02x".format(_)).mkString
  }


  def docSaved(hash: String, conn: Connection): Integer = {
    //val statement = connection.createStatement()
    //val resultSet = statement.executeQuery("SELECT count(*) FROM pdf_documents WHERE hash=")
    var count = 0
    var st: PreparedStatement = null
    var rs: ResultSet = null
    try {
      st = conn.prepareStatement("SELECT count(*) FROM pdf_documents WHERE hash=?");
      st.setString(1, hash)
      rs = st.executeQuery()
      while ( rs.next() ) {
        count = rs.getInt(1)
      }
    } catch {
      case e => e.printStackTrace
    } finally {
      if (rs != null) { rs.close() }
      if (st != null) { st.close() }
    }
    return count
  }

  def saveDoc(r: PdfRecord, st:PreparedStatement) {
//    var st:PreparedStatement = null
//    try {
      st.setString(1, r.hash)
      st.setString(2, r.filename)
      st.setTimestamp(3, r.now)
      st.setInt(4, r.pages)
      st.setString(5, r.title)
      st.setString(6, r.author)
      st.setString(7, r.subject)
      st.setString(8, r.keywords)
      st.setString(9, r.creator)
      st.setString(10, r.producer)
      st.setTimestamp(11, r.created)
      st.setString(12, r.content)
      st.executeUpdate()
  }

  def getDBConnection(driver:String, url:String, username:String, password:String): Connection = {
    //val driver = "com.mysql.jdbc.Driver"
    //val url = "jdbc:mysql://localhost/pdf"
    //val username = "pdf"
    //val password = "pdf1"
    var c:Connection = null
    try {
      Class.forName(driver)
      c = DriverManager.getConnection(url, username, password)
    }
    catch {
      case e => e.printStackTrace
    }
    return c
  }

  def processFile(file: String, connection: Connection, moveFolder: String) {
      val hash = computeHash(file)
      if ( docSaved(hash, connection) != 0) {
          println(s"WARN: Document: $file is already saved under hash: $hash")
          return
      }
      val handler = new BodyContentHandler(-1)
      val metadata = new Metadata()
      val inputstream = new FileInputStream(new File(file))
      val pcontext = new ParseContext()
      
      //parsing pdf document
      val pdfparser = new PDFParser()
      pdfparser.parse(inputstream, handler, metadata,pcontext)
      
      //getting the content
      //println("Contents of the PDF :" + handler.toString())
      val content = handler.toString()
 
      
      //getting metadata of the document
      //println("Metadata of the PDF:")
      val fmt = new java.text.SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
      fmt.setTimeZone(TimeZone.getTimeZone("GMT"))

      val metaNames = metadata.names()
      val now = new Timestamp(new java.util.Date().getTime()) //LocalDateTime.now()
      val keywords = metadata.get("meta:keyword")
      val producer = metadata.get("producer")
      val author = metadata.get("Author")
      val created = new Timestamp(fmt.parse(metadata.get("Creation-Date")).getTime()) //long for UTC time
      val creator = metadata.get("creator")
      val subject = metadata.get("subject")
      val title = metadata.get("title")
      val pages = metadata.get("xmpTPg:NPages").toInt
      val filename = new File(file).getName()
      val r =  PdfRecord(hash, filename, now, keywords, producer, author, creator, created, subject, title, pages, content)

      // Saving to database
      var st: PreparedStatement = null
      try {
        st = connection.prepareStatement("INSERT INTO pdf_documents(hash,filename,parse_dt,page_count,title,author,subject,keywords,creator,producer,created_dt,content) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)")
        saveDoc(r, st)
        println(s"INFO: Document: $file saved with hash: $hash")
        // Moving file which was processed
        val oldfile = new File(file).toPath
        val fname = moveFolder + "/" + filename
        val savfile = new File(fname).toPath
        Files.move(oldfile, savfile, StandardCopyOption.ATOMIC_MOVE)
      }
      catch {
        case e => e.printStackTrace
      } finally {
        if (st != null) {
          st.close();
        }
      }
/*
      println("START =============================> "+file) 
      for(name <- metaNames) { println(name+ " : " + metadata.get(name)) }
      println("END =============================> "+file) 
*/
  }

/*
  def processFileTess4j(file: String, connection: Connection) {
        val parser = new AutoDetectParser()
        val handler = new BodyContentHandler(Integer.MAX_VALUE)

        val config = new TesseractOCRConfig()
        val pdfConfig = new PDFParserConfig()
        pdfConfig.setExtractInlineImages(true)

        val parseContext = new ParseContext()
        parseContext.set(classOf[TesseractOCRConfig], config)
        parseContext.set(classOf[PDFParserConfig], pdfConfig)
        //need to add this to make sure recursive parsing happens!
        parseContext.set(classOf[Parser], parser)

        val stream = new FileInputStream(file)
        val metadata = new Metadata()
        parser.parse(stream, handler, metadata, parseContext)
        println(metadata)
        val content = handler.toString()
        println("====================================>"+file)
        println(content)
        println(metadata)
        println("Done")

  }*/


  def processFiles(folder: String, connection: Connection) {
    val file = new File(folder)
    val list = file.listFiles.filter(_.isFile).filter(_.getName.toLowerCase().endsWith(".pdf")).map(_.getPath).toList
    for (f <- list) {
      processFile(f, connection, folder+"/processed")
    }

  }


  def main(args: Array[String]) {
      if (args.length != 5) {
          println("Usage: pdftodb [pdf folder] [driver] [jdbc url] [username] [password]")
          println("Usage: pdftodb /tmp/pdf/ com.mysql.jdbc.Driver jdbc:mysql://localhost:3306/pdf pdf pdf")
          println("# of arguments: "+args.length)
          throw new IllegalArgumentException("Wrong number of arguments, exiting...");
      }
      val folder = args(0)
      val driver = args(1)
      val url = args(2)
      val username = args(3)
      val password = args(4)
      var connection = getDBConnection(driver, url, username, password)
      processFiles(folder, connection)
      connection.close()

  }

}

/*
METADATA:
date : 2010-10-01T22:47:05Z
pdf:PDFVersion : 1.4
pdf:docinfo:title : AnIntroductiontoMachineLearning
xmp:CreatorTool : LaTeX with hyperref package
Keywords :
access_permission:modify_annotations : true
access_permission:can_print_degraded : true
subject :
dc:creator : AlexJ.SmolaandVishyS.V.N.Vishwanathan
dcterms:created : 2010-10-01T22:47:05Z
Last-Modified : 2010-10-01T22:47:05Z
dcterms:modified : 2010-10-01T22:47:05Z
dc:format : application/pdf; version=1.4
title : AnIntroductiontoMachineLearning
Last-Save-Date : 2010-10-01T22:47:05Z
pdf:docinfo:creator_tool : LaTeX with hyperref package
access_permission:fill_in_form : true
pdf:docinfo:keywords :
pdf:docinfo:modified : 2010-10-01T22:47:05Z
meta:save-date : 2010-10-01T22:47:05Z
pdf:encrypted : false
dc:title : AnIntroductiontoMachineLearning
pdf:docinfo:custom:PTEX.Fullbanner : This is pdfTeX, Version 3.1415926-1.40.10-2.2 (TeX Live/MacPorts 2009_6) kpathsea version 5.0.0
modified : 2010-10-01T22:47:05Z
cp:subject :
pdf:docinfo:subject :
Content-Type : application/pdf
pdf:docinfo:creator : AlexJ.SmolaandVishyS.V.N.Vishwanathan
PTEX.Fullbanner : This is pdfTeX, Version 3.1415926-1.40.10-2.2 (TeX Live/MacPorts 2009_6) kpathsea version 5.0.0
creator : AlexJ.SmolaandVishyS.V.N.Vishwanathan
meta:author : AlexJ.SmolaandVishyS.V.N.Vishwanathan
dc:subject :
trapped : False
meta:creation-date : 2010-10-01T22:47:05Z
created : Fri Oct 01 18:47:05 EDT 2010
access_permission:extract_for_accessibility : true
access_permission:assemble_document : true
xmpTPg:NPages : 234
Creation-Date : 2010-10-01T22:47:05Z
access_permission:extract_content : true
access_permission:can_print : true
pdf:docinfo:trapped : False
meta:keyword :
Author : AlexJ.SmolaandVishyS.V.N.Vishwanathan
producer : pdfTeX-1.40.10
access_permission:can_modify : true
pdf:docinfo:producer : pdfTeX-1.40.10
pdf:docinfo:created : 2010-10-01T22:47:05Z
*/
