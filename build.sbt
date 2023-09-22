import sbt._
import Keys._

val avroVersion = "1.11.2"
val hadoopVersion = "3.3.6"
val parquetVersion = "1.14.0-SNAPSHOT"

 lazy val root = (project in file("."))
   .settings(
     organization := "data",
     name := "parquet-benchmarks",
     scalaVersion := "2.13.8",
     mainClass in (Jmh, run) := Some("data.ReadBenchmark"),
     libraryDependencies ++= Seq(
      "org.apache.hadoop" % "hadoop-client" % hadoopVersion,
      "org.apache.parquet" % "parquet-avro" % parquetVersion,
      "org.apache.parquet" % "parquet-hadoop" % parquetVersion,
      "org.apache.avro" % "avro" % avroVersion,
      "org.apache.avro" % "avro-compiler" % avroVersion
     )
   ).enablePlugins(JmhPlugin)
