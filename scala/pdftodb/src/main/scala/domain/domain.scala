package domain

//import java.time.LocalDateTime
import java.sql.Timestamp

case class PdfRecord(hash: String, filename: String, now: Timestamp, keywords: String, producer: String, author: String, creator: String, created: Timestamp, subject: String, title: String, pages: Integer, content: String)

/*
keywords
producer
author =
created 
creator 
subject 
title = 
pages = 
*/

