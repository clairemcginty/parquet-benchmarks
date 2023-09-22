package data

import org.apache.hadoop.conf.Configuration
import org.apache.parquet.avro.AvroParquetReader
import org.apache.parquet.column.Encoding
import org.apache.parquet.hadoop.ParquetFileReader
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.results.RunResult
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.CommandLineOptions
import testdata.TestRecord

import java.io.{File, FileWriter}
import java.util.concurrent.TimeUnit
import scala.collection.mutable
import scala.jdk.CollectionConverters._

// Run all:
// % sbt "Jmh/run -i 10 -wi 5 -f1 -t1"

// Run for specific config
// % sbt -Dcardinality=50000 "Jmh/run -i 10 -wi 5 -f1 -t1"
object ReadBenchmark {
  case class BenchResult(config: BenchConfig, dictEncodedCols: String, jmhResult: Double)

  implicit val ordering: Ordering[BenchResult] = Ordering.by[BenchResult, BenchConfig](_.config)

  def main(args: Array[String]): Unit = {
    val benchResults = new mutable.ListBuffer[BenchResult]

    // Run benchmarks for each configuration
    BenchConfig.benchConfigurations().foreach { conf =>
      println(s"Benchmarking configuration $conf")

      val opts = new CommandLineOptions(
        (
          args ++ Seq(
            "--jvmArgs",
            s"-D${SysProps.cardinality}=${conf.cardinality}" +
              s" -D${SysProps.ordering}=${conf.recordOrdering}" +
              s" -D${SysProps.distribution}=${conf.distribution}" +
              s" -D${SysProps.compression}=${conf.compression.toString}" +
              s" -D${SysProps.pageSizeMb}=${conf.pageSizeMb.toString}"
          )
          ):_*)

      val runner = new Runner(opts)
      val results = runner.run()

      // Write benchmark results to file
      println("Finished benchmarks, computing results...")

      // Compute which cols have dictionaries enabled
      val reader = ParquetFileReader.open(new Configuration(), conf.path)
      val colsWithDicts = reader.getRowGroups.get(0).getColumns.asScala
        .map(col => (col.getPath.toArray.head.toLowerCase, (col.getEncodings.contains(Encoding.PLAIN_DICTIONARY) || col.getEncodings.contains(Encoding.RLE_DICTIONARY))))
        .toMap
      reader.close()

      results.asScala.toList.sortBy(_.getParams.getBenchmark).foreach { result: RunResult ⇒
        benchResults += BenchResult(
          conf,
          s"${colsWithDicts.filter(_._2).keys.mkString("<br/>")}",
          result.getPrimaryResult.getScore)
      }
    }

    val resultFile = new File("read_results.md")

    // Write file header
    val sb = new mutable.StringBuilder
    sb ++= "| Cardinality | Page Size | Compression | Distribution | Sorting | Dict-Encoded Cols | Extra Conf | Read Time |\n"
    sb ++= "|-------------|-----------|-------------|-----------|----------|-------------------|------------|-----------|\n"
    val f = new FileWriter(resultFile, true)
    f.write(sb.result)

    benchResults.sorted.foreach { br =>
      f.write(
        s"| ${br.config.cardinality} | ${br.config.pageSizeMb} MB | ${br.config.compression} | ${br.config.distribution} | ${br.config.recordOrdering} | ${br.dictEncodedCols} | None | ${br.jmhResult / 1000.0} s |\n"
      )
    }
    f.close()
  }
}

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
class ReadBenchmark {
  import BenchConfig._

  def getConfig(): BenchConfig = BenchConfig(
    SysProps.getCardinality.get,
    SysProps.getCompression.get,
    SysProps.getPageSizeMb.get,
    SysProps.getOrdering.get,
    SysProps.getDistribution.get
  )

  @Benchmark
  def readFile(): Unit = {
   val reader = new AvroParquetReader[TestRecord](getConfig().path)
    (0 to NumRecords).foreach(_ => reader.read())
    reader.close()
  }
}
