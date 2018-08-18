Prerequisites for pdftodb application

# MySQL
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

#Oracle
DROP TABLE pdf_documents;
CREATE TABLE pdf_documents (
  hash varchar2(64) NOT NULL,
  filename varchar2(128) NOT NULL,
  parse_dt DATETIME NOT NULL,
  page_count INTEGER DEFAULT NULL,
  title varchar2(256) DEFAULT NULL,
  author varchar2(256) DEFAULT NULL,
  subject varchar2(256) DEFAULT NULL,
  keywords varchar2(256) DEFAULT NULL,
  creator   varchar2(256) DEFAULT NULL,
  producer varchar2(256) DEFAULT NULL,
  created_dt DATETIME DEFAULT NULL,
  content CLOB NULL
);
CREATE INDEX pdf_documents_ix1 on pdf_documents(hash);


# SQL queries
select hash, filename, parse_dt, page_count, title, author, subject, keywords, creator, producer, created_dt, length(content) from pdf_documents \G


# How to compile and run

## MySQL:
sbt "run /data/datasource/pdf com.mysql.jdbc.Driver jdbc:mysql://localhost:3306/pdf?autoReconnect=true pdf pdf"

## Oracle:
mvn install:install-file -Dfile=C:\\app\\oracle\\product\\12.2.0\\dbhome_1\\jdbc\\lib\\ojdbc8.jar \
-DgroupId=com.oracle -DartifactId=ojdbc8 -Dversion=12.2.0 -Dpackaging=jar #-DgeneratePom=true


sbt "run /data/datasource/pdf oracle.jdbc.OracleDriver jdbc:oracle:thin:@localhost:1521:pdf pdf pdf"
#jdbcurl = jdbc:oracle:<drivertype>:@<hostname>:<port>:<database>
