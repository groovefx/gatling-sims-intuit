package sims.intuit.trips

import actions.intuit.TripActions
import com.datastax.gatling.plugin.CqlPredef._
import com.datastax.gatling.stress.core.BaseSimulation
import com.datastax.gatling.stress.libs.{FetchBaseData, SimConfig}
import feeds.intuit.{TripVehicleFeed, TripVehicleSolrFeed}
import io.gatling.core.Predef._

class CombinedWriteAndQueryTripsSimulation extends BaseSimulation {

  val simName = "intuitTrips"

  // start Query Scenario
  val queryScenarioName = "combinedQueryTrips"
  val querySimConf = new SimConfig(conf, simName, queryScenarioName)
  val queryActions = new TripActions(cass, querySimConf)
  val solrFeed = new TripVehicleSolrFeed

  // create base data file using config values
  new FetchBaseData(querySimConf, cass).createBaseDataCsv()
  val feederFile = getDataPath(querySimConf)
  val csvFeeder = csv(feederFile).random

  val queryScenario = Scenarios.getSolrScenario(csvFeeder, queryActions, solrFeed)


  // start Write Scenario
  val writeScenarioName = "combinedWriteVehicleAndTrips"
  val writeSimConf = new SimConfig(conf, simName, writeScenarioName)
  val writeFeeds = new TripVehicleFeed(writeSimConf)
  val writeScenario = Scenarios.getWriteScenario(writeFeeds, queryActions)


  setUp(
    loadGenerator.rampUpToPercentage(queryScenario, querySimConf),
    loadGenerator.rampUpToPercentage(writeScenario, writeSimConf)
  ).protocols(cqlProtocol)
}