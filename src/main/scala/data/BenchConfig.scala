package data

import org.apache.hadoop.fs.Path
import org.apache.parquet.avro.AvroParquetWriter
import org.apache.parquet.hadoop.ParquetWriter
import org.apache.parquet.hadoop.metadata.CompressionCodecName
import testdata._

import java.io.File
import scala.util.Random

object BenchConfig {
  val Blocksize_256_Mb = 256 * 1024 * 1024L // 256 MB
  val NumRecords = 5_000_000

  val directory = {
    val f = new File("testdata/")
    f.mkdir()
    f
  }

  implicit val confOrdering: Ordering[BenchConfig] = Ordering.by[BenchConfig, Int](_.cardinality)
    .orElseBy(_.distribution.toString)
    .orElseBy(_.recordOrdering.toString)
    .orElseBy(_.pageSizeMb)
    .orElseBy(_.compression)

  def benchConfigurations(): List[BenchConfig] = {
    val cardinalities = SysProps.getCardinality.map(List(_)).getOrElse(allCardinalities)
    val pageSizeMbs = SysProps.getPageSizeMb.map(List(_)).getOrElse(allPageSizeMbs)
    val distributions = SysProps.getDistribution.map(List(_)).getOrElse(allDistributions)
    val orderings = SysProps.getOrdering.map(List(_)).getOrElse(allOrderings)
    val compressions = SysProps.getCompression.map(List(_)).getOrElse(allCompressions)

    val all = for {
      cardinality <- cardinalities;
      pageSizeMb <- pageSizeMbs;
      distribution <- distributions;
      ordering <- orderings;
      compression <- compressions
    } yield BenchConfig(cardinality, compression, pageSizeMb, ordering, distribution)

    // @todo not sure these filters work....
    all
      .filter(conf => SysProps.getCardinality.forall(b => conf.cardinality == b))
      .filter(conf => SysProps.getCompression.forall(b => conf.compression == b))
      .filter(conf => SysProps.getPageSizeMb.forall(b => conf.pageSizeMb == b))
      .filter(conf => SysProps.getOrdering.forall(b => conf.recordOrdering == b))
      .filter(conf => SysProps.getDistribution.forall(b => conf.distribution == b))
  }


  val allCardinalities: List[Int] = List(1000, 5000, 50000, 100000, 1000000)
  val allPageSizeMbs: List[Int] = List(1, 10)
  val allOrderings: List[RecordOrdering] = List(Sorted, Shuffled)
  val allDistributions: List[DataDistribution] = List(RoundRobin, Normal)
  val allCompressions: List[CompressionCodecName] = List(CompressionCodecName.UNCOMPRESSED, CompressionCodecName.ZSTD)
}

case class BenchConfig(
  cardinality: Int,
  compression: CompressionCodecName,
  pageSizeMb: Int = 1,
  recordOrdering: RecordOrdering,
  distribution: DataDistribution
) {
  import BenchConfig._

  lazy val file: File =
    new File(
      directory,
      s"${compression}/pageSizeMb_${pageSizeMb}/sorting_${recordOrdering}_dist_${distribution}_cardinality${cardinality}.parquet"
    )

  lazy val path = new Path(file.getAbsolutePath)

  lazy val isParquetDefault: Boolean =
      compression == CompressionCodecName.UNCOMPRESSED && pageSizeMb == 1

  def records(): Seq[TestRecord] = {
    lazy val random = new Random

    val all = (0 until NumRecords).map { i =>
      val value = distribution match {
        case Normal => (random.nextGaussian() * cardinality).toInt
        case RoundRobin => i % cardinality
      }

      TestRecord
        .newBuilder()
        .setDoubleField(value.toDouble)
        .setStringField(value.toString)
        .setIntField(value)
        .build()
    }

    recordOrdering match {
      case Sorted => all.sortBy(_.intField)
      case Shuffled => Random.shuffle(all)
    }
  }

  def writer(): ParquetWriter[TestRecord] = {
    println(s"Writing to path $path with compression $compression and page size ${pageSizeMb} MB.")
    AvroParquetWriter.builder[TestRecord](path)
      .withSchema(testdata.TestRecord.SCHEMA$)
      .withCompressionCodec(compression)
      .withRowGroupSize(Blocksize_256_Mb)
      .withPageSize(1024 * 1024 * pageSizeMb)
      .build()
  }
}

sealed trait RecordOrdering
object RecordOrdering {
  def parse(str: String): RecordOrdering = str match {
    case "Sorted" => Sorted
    case "Shuffled" => Shuffled
  }
}
case object Sorted extends RecordOrdering {
  override def toString: String = "Sorted"
}
case object Shuffled extends RecordOrdering {
  override def toString: String = "Shuffled"
}

sealed trait DataDistribution
object DataDistribution {
  def parse(str: String): DataDistribution = str match {
    case "RoundRobin" => RoundRobin
    case "Normal" => Normal
  }
}
case object RoundRobin extends DataDistribution {
  override def toString: String = "RoundRobin"
}
case object Normal extends DataDistribution {
  override def toString: String = "Normal"
}
