package crud.server.rest.metrics

import crud.server.api.metrics.{CounterMetricDef, MetricsRegistry}
import io.prometheus.client.Counter

object EndpointMetrics {
  val clientsCounter: Counter = MetricsRegistry.get(CounterMetricDef("endpoint_clients", "count of requests on endpoint /clients", "method"))
  val clientsIdCounter: Counter = MetricsRegistry.get(CounterMetricDef("endpoint_clients_id", "count of requests on endpoint /clients/:id", "method"))
}
