package crud.server.api.metrics

import io.prometheus.client
import io.prometheus.client.exporter.common.TextFormat
import io.prometheus.client._

import java.io.StringWriter
import java.util
import scala.collection.mutable

object MetricsRegistry {
  private val registry = new mutable.HashMap[String, SimpleCollector[_]]()

  private def familySamples: util.List[Collector.MetricFamilySamples] = {
    type A = util.ArrayList[Collector.MetricFamilySamples]
    type B = util.List[Collector.MetricFamilySamples]
    def reduceF(x1: A, x2: B): A = {
      x1 addAll x2
      x1
    }

    registry.values
      .map {
        case gauge: Gauge                    => gauge.collect()
        case counter: Counter                => counter.collect()
        case info: Info                      => info.collect()
        case summary: Summary                => summary.collect()
        case histogram: Histogram            => histogram.collect()
        case enumeration: client.Enumeration => enumeration.collect()
      }
      .foldLeft(new util.ArrayList[Collector.MetricFamilySamples]())(reduceF)
  }

  def get[C <: SimpleCollector[_]](metricDef: MetricDef[C, _]): C =
    registry.getOrElseUpdate(metricDef.name, metricDef.build).asInstanceOf[C]

  def export: String = {
    val writer = new StringWriter()
    TextFormat.write004(
      writer,
      new util.Enumeration[Collector.MetricFamilySamples] {
        val iterator: util.Iterator[Collector.MetricFamilySamples] = familySamples.iterator()

        override def hasMoreElements: Boolean = iterator.hasNext

        override def nextElement(): Collector.MetricFamilySamples = iterator.next()
      }
    )
    writer.toString
  }
}
