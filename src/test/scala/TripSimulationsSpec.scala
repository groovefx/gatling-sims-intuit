/**
  * Created by bradvernon on 6/15/17.
  */

import io.gatling.app.Gatling
import io.gatling.core.config.GatlingPropertiesBuilder
import org.scalatest.{FlatSpec, Ignore}

@Ignore
class TripSimulationsSpec extends FlatSpec {

  val props = new GatlingPropertiesBuilder

  "WriteRampToVehicleAndTripsSimulation" should "succeed with 0 failures" in {
    props.simulationClass("sims.intuit.trips.WriteRampToVehicleAndTripsSimulation") //put your class name here
    assert(Gatling.fromMap(props.build).equals(0))
  }

  "WriteConstantToVehicleAndTripsSimulation" should "succeed with 0 failures" in {
    props.simulationClass("sims.intuit.trips.WriteConstantToVehicleAndTripsSimulation") //put your class name here
    assert(Gatling.fromMap(props.build).equals(0))
  }

  "SolrQueryTripsSimulation" should "succeed with 0 failures" in {
    props.simulationClass("sims.intuit.trips.SolrQueryTripsSimulation") //put your class name here
    assert(Gatling.fromMap(props.build).equals(0))
  }

  "CombinedWriteAndQueryTripsSimulation" should "succeed with 0 failures" in {
    props.simulationClass("sims.intuit.trips.CombinedWriteAndQueryTripsSimulation") //put your class name here
    assert(Gatling.fromMap(props.build).equals(0))
  }

}
