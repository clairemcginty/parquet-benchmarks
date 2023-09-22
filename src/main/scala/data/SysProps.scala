package data

import org.apache.parquet.hadoop.metadata.CompressionCodecName

object SysProps {
  val cardinality = "numUniqueColValues"
  def getCardinality = Option(System.getProperty(cardinality)).map(_.toInt)

  val distribution = "distribution"
  def getDistribution = Option(System.getProperty(distribution)).map(DataDistribution.parse)

  val ordering = "ordering"
  def getOrdering = Option(System.getProperty(ordering)).map(RecordOrdering.parse)

  val compression = "compression"
  def getCompression = Option(System.getProperty(compression)).map(CompressionCodecName.valueOf)

  val pageSizeMb = "pageSizeMb"
  def getPageSizeMb = Option(System.getProperty(pageSizeMb)).map(_.toInt)
}
