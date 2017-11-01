package sims.intuit.integrations

import actions.intuit.IntegrationActions
import com.datastax.gatling.plugin.CqlPredef._
import com.datastax.gatling.stress.core.BaseSimulation
import com.datastax.gatling.stress.libs.{FetchBaseData, SimConfig}
import feeds.intuit.{IntegrationFeed, IntegrationSolrFeed}
import io.gatling.core.Predef._

class CombinedWriteAndQueryStageEntitiesSimulation extends BaseSimulation {

  val simName = "intuitIntegration"

  // start Query Scenario
  val queryScenarioName = "combinedQueryStageEntities"
  val querySimConf = new SimConfig(conf, simName, queryScenarioName)
  val queryActions = new IntegrationActions(cass, querySimConf)
  val solrFeed = new IntegrationSolrFeed

  // create base data file using config values
  new FetchBaseData(querySimConf, cass).createBaseDataCsv()
  val feederFile = getDataPath(querySimConf)
  val csvFeeder = csv(feederFile).random

  val queryScenario = IntegrationScenarios.getSolrScenario(csvFeeder, queryActions, solrFeed)


  // start Write Scenario
  val writeScenarioName = "combinedWriteStageEntities"
  val writeSimConf = new SimConfig(conf, simName, writeScenarioName)
  val writeFeeds = new IntegrationFeed(writeSimConf)
  val writeScenario = IntegrationScenarios.getWriteScenario(writeFeeds, queryActions)


  setUp(
    loadGenerator.rampUpToPercentage(queryScenario, querySimConf),
    loadGenerator.rampUpToPercentage(writeScenario, writeSimConf)
  ).protocols(cqlProtocol)
}
