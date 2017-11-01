package sims.intuit.integrations

import actions.intuit.IntegrationActions
import feeds.intuit.{IntegrationFeed, IntegrationSolrFeed}
import io.gatling.core.Predef._
import io.gatling.core.feeder.RecordSeqFeederBuilder
import io.gatling.core.structure.ScenarioBuilder

object IntegrationScenarios {

  def getSolrScenario(csvFeeder: RecordSeqFeederBuilder[String], actions: IntegrationActions, solrFeed: IntegrationSolrFeed): ScenarioBuilder = {
    scenario("SolrQuery")
        .feed(csvFeeder)
        .exec { session =>
          val realmId = session.attributes("realm_id")
          session.setAll(Map("solr_query" -> solrFeed.getSolrQuery(realmId.toString)))
        }
        .exec(actions.queryWithSolr)
  }

  def getWriteScenario(writeFeeds: IntegrationFeed, actions: IntegrationActions): ScenarioBuilder = {
    scenario("Insert")
        .feed(writeFeeds.writeRealm)
        .repeat("${rows_per_realm}") {
          feed(writeFeeds.writeStageEntity)
              .exec(actions.writeStageEntity)
        }
  }

}
