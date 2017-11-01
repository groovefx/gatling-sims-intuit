package sims.intuit.trips

import actions.intuit.TripActions
import com.datastax.gatling.plugin.CqlPredef._
import com.datastax.gatling.stress.core.BaseSimulation
import com.datastax.gatling.stress.libs.{FetchBaseData, SimConfig}
import feeds.intuit.TripVehicleSolrFeed
import io.gatling.core.Predef._
import io.gatling.core.feeder.RecordSeqFeederBuilder

class SolrQueryTripsSimulation extends BaseSimulation {

  val simName = "intuitTrips"
  val scenarioName = "queryTrips"

  val simConf = new SimConfig(conf, simName, scenarioName)

  val actions = new TripActions(cass, simConf)

  val solrFeed = new TripVehicleSolrFeed

  // create base data file using config values
  new FetchBaseData(simConf, cass).createBaseDataCsv()

  val feederFile = getDataPath(simConf)
  val csvFeeder: RecordSeqFeederBuilder[String] = csv(feederFile).random

  val writeScenario = Scenarios.getSolrScenario(csvFeeder, actions, solrFeed)

  setUp(

    loadGenerator.rampUpToConstant(writeScenario, simConf)

  ).protocols(cqlProtocol)
}
