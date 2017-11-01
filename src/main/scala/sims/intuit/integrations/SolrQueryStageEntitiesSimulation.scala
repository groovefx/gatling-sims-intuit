package sims.intuit.integrations

import actions.intuit.IntegrationActions
import com.datastax.gatling.plugin.CqlPredef._
import com.datastax.gatling.stress.core.BaseSimulation
import com.datastax.gatling.stress.libs.{FetchBaseData, SimConfig}
import feeds.intuit.IntegrationSolrFeed
import io.gatling.core.Predef._
import io.gatling.core.feeder.RecordSeqFeederBuilder

class SolrQueryStageEntitiesSimulation extends BaseSimulation {

  val simName = "intuitIntegration"
  val scenarioName = "queryEntities"

  val simConf = new SimConfig(conf, simName, scenarioName)
  val actions = new IntegrationActions(cass, simConf)
  val solrFeed = new IntegrationSolrFeed

  // create base data file using config values
  new FetchBaseData(simConf, cass).createBaseDataCsv()

  val feederFile = getDataPath(simConf)
  val csvFeeder: RecordSeqFeederBuilder[String] = csv(feederFile).random

  val writeScenario = IntegrationScenarios.getSolrScenario(csvFeeder, actions, solrFeed)

  setUp(

    loadGenerator.rampUpToConstant(writeScenario, simConf)

  ).protocols(cqlProtocol)
}
