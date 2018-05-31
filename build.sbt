name := "spleendao"

version := "0.1.0"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-dbcp2" % "2.0",
  "mysql" % "mysql-connector-java" % "5.1.16",
  "org.postgresql" % "postgresql" % "42.2.2"
)
