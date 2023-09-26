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

  val zstdCompressionLevel = "zstdCompressionLevel"
  def getZstdCompressionLevel = Option(System.getProperty(zstdCompressionLevel)).map(_.toInt)

  val pageSizeMb = "pageSizeMb"
  def getPageSizeMb = Option(System.getProperty(pageSizeMb)).map(_.toInt)

  val dictPageSizeMb = "dictPageSizeMb"
  def getDictPageSizeMb = Option(System.getProperty(dictPageSizeMb)).map(_.toInt)

  val maxDictionaryCompressionRatio = "maxDictionaryCompressionRatio"
  def getMaxDictionaryCompressionRatio = Option(System.getProperty(maxDictionaryCompressionRatio)).map(_.toDouble)
}
