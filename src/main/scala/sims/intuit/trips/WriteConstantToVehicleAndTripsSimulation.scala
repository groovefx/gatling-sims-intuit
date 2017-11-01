package sims.intuit.trips

import actions.intuit.TripActions
import com.datastax.gatling.plugin.CqlPredef._
import com.datastax.gatling.stress.core.BaseSimulation
import com.datastax.gatling.stress.libs.SimConfig
import feeds.intuit.TripVehicleFeed

class WriteConstantToVehicleAndTripsSimulation extends BaseSimulation {

  val simName = "intuitTrips"
  val scenarioName = "writeConstantToVehicleAndTrips"

  val simConf = new SimConfig(conf, simName, scenarioName)

  val actions = new TripActions(cass, simConf)

  val feeds = new TripVehicleFeed(simConf)

  val writeScenario = Scenarios.getWriteScenario(feeds, actions)

  setUp(

    loadGenerator.constantToTotal(writeScenario, simConf)

  ).protocols(cqlProtocol)
}