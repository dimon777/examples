sbt "run /data/datasource/pdf com.mysql.jdbc.Driver jdbc:mysql://localhost:3306/pdf?autoReconnect=true pdf pdf"

select parse_dt, page_count, title, author, subject, keywords, creator, producer, created_dt from pdf_documents 
