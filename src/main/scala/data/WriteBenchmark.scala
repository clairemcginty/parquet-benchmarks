package data

import org.apache.hadoop.conf.Configuration
import org.apache.parquet.column.Encoding
import org.apache.parquet.hadoop.ParquetFileReader
import org.apache.parquet.hadoop.metadata.CompressionCodecName

import java.io.FileWriter
import java.nio.file.Files
import scala.collection.mutable
import scala.jdk.CollectionConverters._

// Write specific configuration:
// % sbt -Dcardinality=50000 "runMain data.WriteBenchmark"

// Write all
// % sbt "runMain data.WriteBenchmark"
object WriteBenchmark {
  import BenchConfig._

  def main(cmdLineArgs: Array[String]): Unit = {
    val benchResults = new mutable.ListBuffer[(BenchConfig, String)]

    BenchConfig.benchConfigurations().foreach { conf =>
      println(s"Benchmarking configuration $conf")

      conf.file.mkdirs()
      conf.file.delete() // overwrite file

      val writer = conf.writer()

      val start = System.currentTimeMillis()
      conf.records().foreach(writer.write)
      val end = System.currentTimeMillis()

      writer.close()

      // Validate columns: if there are too many unique vals for a column, dictionary encoding may fail
      val reader = ParquetFileReader.open(new Configuration(), conf.path)
      val cols = reader.getRowGroups.get(0).getColumns.asScala
      val dictCols = cols.flatMap { c =>
        val hasDictEncoding = c.getEncodings.contains(Encoding.PLAIN_DICTIONARY) || c.getEncodings.contains(Encoding.RLE_DICTIONARY)
        if (!hasDictEncoding) {
          println(s"WARNING: Conf<${conf}> specified dictionary encoding, but column ${c.getPath} contained only: ${c.getEncodings}. Maybe # unique values was too big?")
        }

        if (hasDictEncoding) Some(c.getPath.toArray.head) else None
      }

      println(s"Finished writing ${conf.file.getAbsolutePath}.")
      val s = if (conf.isParquetDefault) {
        s"| **${conf.cardinality}** | **${dictCols.mkString("<br/>")}** | **${conf.compressionStr}** | **${conf.pageSizeMb} MB** | **${conf.dictPageSizeMb} MB** | **${conf.distribution}** | **${conf.recordOrdering}** | **${conf.extraProp.map(_.toString).getOrElse("")}** | **${Files.size(conf.file.toPath) / 1024.0 / 1024.0} MB**  | **${(end - start) / 1000.0} s** |\n"
      } else {
        s"| ${conf.cardinality} | ${dictCols.mkString("<br/>")} | ${conf.compressionStr} | ${conf.pageSizeMb} MB | ${conf.dictPageSizeMb} MB | ${conf.distribution} | ${conf.recordOrdering} | ${conf.extraProp.map(_.toString).getOrElse("")} | ${Files.size(conf.file.toPath) / 1024.0 / 1024.0} MB  | ${(end - start) / 1000.0} s |\n"
      }

      benchResults += ((conf, s))
    }

    // Write file header
    val sb = new mutable.StringBuilder
    sb ++= "| Cardinality | Dict-Encoded Cols | Compression | Page Size | Dict Page Size | Data Distribution | Sorting | Extra Props | File Size | Write Time |\n"
    sb ++= "|-------------|-------------------|-------------|-----------|-----------|-------------------|---------|-------------|-----------|------------|\n"

    val f = new FileWriter("write_results.md", true)
    f.write(sb.result)

    benchResults
      .toList
      .sortBy(_._1)
      .map(_._2)
      .foreach(f.write)

    f.close()
    println(s"Finished writing all Parquet files; bench data written to write_results.md.")
  }
}
