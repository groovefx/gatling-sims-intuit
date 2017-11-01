package feeds.intuit

import com.datastax.gatling.stress.core.BaseFeed
import com.typesafe.scalalogging.LazyLogging
import feeds.intuit.solr.SolrQueryBuilder

import scala.collection.mutable.ListBuffer

class TripVehicleSolrFeed extends BaseFeed with LazyLogging {

  def getSolrQuery(realmId: String, vehicleId: String): String = {

    val solrQueries = ListBuffer[String]()
    val reviewedState = getRandom(Array("PERSONAL", "BUSINESS", "UNREVIEWED", "BUSINESS OR PERSONAL"))

    // query={"q":"*:*","fq":["{!cache = false}realm_id:123145820235872","deleted:false","review_state:BUSINESS",
    // "{!cache = false}vehicle_id:95f45d10-8ea4-11e7-a823-9136d9671104","{!cache = false}
    // start_datetime:[2017-01-01T00:00:00.000Z TO *]","{!cache = false}start_datetime:[* TO 2017-12-31T23:59:59.999Z]"],
    // "start":"0","route.partition":["123145820235872"]}
    val searchByVehicleAndStartDate = if (vehicleId.nonEmpty) {
      new SolrQueryBuilder()
          .addFilterQuery(s"realm_id:$realmId", cached = false)
          .addFilterQuery(s"review_state:($reviewedState)")
          .addQuery(s"vehicle_id:$vehicleId AND start_datetime:[2017-01-01T00:00:00.000Z TO 2017-12-31T23:59:59.999Z] AND deleted:false")
          .addSort("start_datetime", "desc")
          .addRoutePartition(List(realmId))
          .getSolrQuery
    } else {
      new SolrQueryBuilder()
          .addFilterQuery(s"realm_id:$realmId", cached = false)
          .addFilterQuery(s"review_state:($reviewedState)")
          .addQuery(s"start_datetime:[2017-01-01T00:00:00.000Z TO 2017-12-31T23:59:59.999Z] AND deleted:false")
          .addSort("start_datetime", "desc")
          .addRoutePartition(List(realmId))
          .getSolrQuery
    }

    //  query={"q":"*:*","fq":["{!cache = false}realm_id:193514610765729","deleted:false","review_state:BUSINESS",
    // "{!cache = false}vehicle_id:68926460-9e31-11e7-bf34-3f738fda69af","{!cache = false}start_datetime:[2016-01-01T00:00:00.000Z TO *]",
    // "{!cache = false}start_datetime:[* TO 2016-12-31T23:59:59.999Z]"],"start":"0","route.partition":["193514610765729"]}
    val searchByVehicleAndUpdatedDate = if (vehicleId.nonEmpty) {
      new SolrQueryBuilder()
          .addFilterQuery(s"realm_id:$realmId", cached = false)
          //          .addFilterQuery("deleted:false")
          .addFilterQuery(s"review_state:($reviewedState)")
          .addQuery(s"vehicle_id:$vehicleId AND start_datetime:[2016-01-01T00:00:00.000Z TO 2016-12-31T23:59:59.999Z] AND deleted:false")
          .addSort("date_updated", "desc")
          .addRoutePartition(List(realmId))
          .getSolrQuery
    } else {
      new SolrQueryBuilder()
          .addFilterQuery(s"realm_id:$realmId", cached = false)
          //          .addFilterQuery("deleted:false")
          .addFilterQuery(s"review_state:($reviewedState)")
          .addQuery(s"start_datetime:[2016-01-01T00:00:00.000Z TO 2016-12-31T23:59:59.999Z] AND deleted:false")
          .addSort("date_updated", "desc")
          .addRoutePartition(List(realmId))
          .getSolrQuery
    }


    solrQueries.append(
      // "sort":"start_datetime desc ","start":"0","route.partition":["123145820235872"]}
      new SolrQueryBuilder()
          .addFilterQuery(s"realm_id:$realmId", cached = false)
          .addFilterQuery("deleted:false")
          .addSort("start_datetime", "desc")
          .addRoutePartition(List(realmId))
          .getSolrQuery,

      // query={"q":"*:*","fq":["{!cache = false}realm_id:193514610765729","deleted:false","review_state:(BUSINESS PERSONAL)"],
      // "sort":"date_updated desc ","start":"0","route.partition":["193514610765729"]}

      new SolrQueryBuilder()
          .addFilterQuery(s"realm_id:$realmId", cached = false)
          //          .addFilterQuery("deleted:false")
          .addFilterQuery(s"review_state:($reviewedState)")
          .addSort("date_updated", "desc")
          .addRoutePartition(List(realmId))
          .getSolrQuery,

      searchByVehicleAndStartDate,
      searchByVehicleAndUpdatedDate
    )

    val query = getRandom(solrQueries.toList)
    //    logger.warn(query)
    query
  }

}

