package sims.intuit.integrations

import actions.intuit.IntegrationActions
import com.datastax.gatling.plugin.CqlPredef._
import com.datastax.gatling.stress.core.BaseSimulation
import com.datastax.gatling.stress.libs.SimConfig
import feeds.intuit.IntegrationFeed

class WriteConstantToStageEntitiesSimulation extends BaseSimulation {

  val simName = "intuitIntegration"
  val scenarioName = "writeConstantToStageEntities"

  val simConf = new SimConfig(conf, simName, scenarioName)

  val actions = new IntegrationActions(cass, simConf)

  val feeds = new IntegrationFeed(simConf)

  val writeScenario = IntegrationScenarios.getWriteScenario(feeds, actions)

  setUp(

    loadGenerator.constantToTotal(writeScenario, simConf)

  ).protocols(cqlProtocol)
}
