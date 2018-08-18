name := "pdftodb"

logLevel := Level.Error

version := "1.0"

scalaVersion := "2.11.6"

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
//  "org.apache.pdfbox" % "pdfbox" % "1.8.15",
  "org.apache.tika" % "tika-parsers" % "1.18",
//  "com.levigo.jbig2" % "levigo-jbig2-imageio" % "1.6.5",
//  "net.sourceforge.tess4j" % "tess4j" % "3.4.8",
  "com.oracle" % "ojdbc8" % "12.2.0",
  "mysql" % "mysql-connector-java" % "5.1.38"
)

/*resolvers ++= Seq(
      Resolver.sonatypeRepo("public"),
      "Confluent Maven Repo" at "http://packages.confluent.io/maven/"
)*/

