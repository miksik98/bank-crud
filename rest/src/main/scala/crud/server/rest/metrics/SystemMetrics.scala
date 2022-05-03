package crud.server.rest.metrics

import crud.server.api.metrics.{GaugeMetricDef, MetricsRegistry}
import io.prometheus.client.Gauge

object SystemMetrics {
  val systemStartGauge: Gauge = MetricsRegistry.get(GaugeMetricDef("system_start", "exact time when system was started"))
}
