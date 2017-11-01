package sims.intuit.trips

import java.util.UUID

import actions.intuit.TripActions
import feeds.intuit.{TripVehicleFeed, TripVehicleSolrFeed}
import io.gatling.core.Predef._
import io.gatling.core.feeder.RecordSeqFeederBuilder
import io.gatling.core.structure.ScenarioBuilder

object Scenarios {

  def getSolrScenario(csvFeeder: RecordSeqFeederBuilder[String], actions: TripActions, solrFeed: TripVehicleSolrFeed): ScenarioBuilder = {
    scenario("SolrQuery")
        .feed(csvFeeder)
        .exec { session =>
          val vehicle_id = session.attributes("vehicle_id")
          val realmId = session.attributes("realm_id")
          session.setAll(Map("solr_query" -> solrFeed.getSolrQuery(realmId.toString, vehicle_id.toString)))
        }
        .exec(actions.queryWithSolr)
  }



  def getWriteScenario(writeFeeds: TripVehicleFeed, actions: TripActions): ScenarioBuilder = {
    scenario("Insert")
        .feed(writeFeeds.writeRealm)
        .repeat("${vehicle_cnt}") {
          feed(writeFeeds.writeVehicle)
              .exec(actions.writeVehicle)
        }
        .exec { session =>
          val vehicle_id = session.attributes("vehicle_id").asInstanceOf[UUID]
          session.setAll(session.attributes ++ Map("vehicle_id" -> writeFeeds.getRandomFromSeq(Seq(vehicle_id, None, None))))
        }
        .repeat("${trip_cnt}") {
          feed(writeFeeds.writeTrip)
              .exec(actions.writeTrip)
        }
  }


}
