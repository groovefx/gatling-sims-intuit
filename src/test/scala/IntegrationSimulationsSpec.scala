/**
  * Created by bradvernon on 6/15/17.
  */

import io.gatling.app.Gatling
import io.gatling.core.config.GatlingPropertiesBuilder
import org.scalatest.FlatSpec

//@Ignore
class IntegrationSimulationsSpec extends FlatSpec {

  val props = new GatlingPropertiesBuilder

  "WriteConstantToStageEntitiesSimulation" should "succeed with 0 failures" in {
    props.simulationClass("sims.intuit.integrations.WriteConstantToStageEntitiesSimulation") //put your class name here
    assert(Gatling.fromMap(props.build).equals(0))
  }
}
