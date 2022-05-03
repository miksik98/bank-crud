package crud.server.api.metrics

import io.prometheus.client.SimpleCollector.Builder
import io.prometheus.client.{Counter, Gauge, SimpleCollector}

sealed trait MetricDef[C <: SimpleCollector[_], B <: Builder[B, C]] {
  def builder: B
  def name: String
  def help: String
  def labels: Seq[String]
  def build: C = builder.name(nameValidated).help(help).labelNames(labels: _*).create().register()

  protected def nameValidated: String = name.replaceAll("[^a-zA-Z0-9:_]", "_")
}

case class CounterMetricDef(name: String, help: String, labels: String*) extends MetricDef[Counter, Counter.Builder] {
  override def builder: Counter.Builder = Counter.build()
}

case class GaugeMetricDef(name: String, help: String, labels: String*) extends MetricDef[Gauge, Gauge.Builder] {
  override def builder: Gauge.Builder = Gauge.build()
}

