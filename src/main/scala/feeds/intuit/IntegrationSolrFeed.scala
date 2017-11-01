package feeds.intuit

import com.datastax.gatling.stress.core.BaseFeed
import com.typesafe.scalalogging.LazyLogging
import feeds.intuit.solr.SolrQueryBuilder

import scala.collection.mutable.ListBuffer

class IntegrationSolrFeed extends BaseFeed with LazyLogging {

  def getSolrQuery(realmId: String): String = {

    /** *
      * *
      * select * from integrations.stage_entities where solr_query='realm_id:223146344061365 AND status:TO_BE_REVIEWED';
      * *
      * select * from integrations.stage_entities where solr_query='{"q":"realm_id:223146344061365 AND
      * status:TO_BE_REVIEWED","start":"0","route.partition":["223146344061365"]}';
      * *
      * select * from integrations.stage_entities where solr_query='{"q":"realm_id:223146344061365 AND
      * (status:TO_BE_REVIEWED AND connection_account_id:8d3f3920-65e0-11e7-be32-d933d5d0997e)","start":"0","route.partition":["223146344061365"]}';
      * *
      * select * from integrations.stage_entities where solr_query='{"q":"realm_id:223146344061365 AND
      * (status:TO_BE_REVIEWED AND connection_account_id:(8d3f3920-65e0-11e7-be32-d933d5d0997e aa92f800-a23e-11e7-8884-7b3788d69d8f))",
      * "start":"0","route.partition":["223146344061365"]}';
      * *
      * select * from integrations.stage_entities where solr_query='{"q":"realm_id:223146344061365 AND
      * (status:TO_BE_REVIEWED AND connection_account_id:8d3f3920-65e0-11e7-be32-d933d5d0997e AND
      * transaction_date:[2016-01-01T07:00:00.000Z TO 2018-01-01T07:00:00.000Z])","start":"0","route.partition":["223146344061365"]}';
      * *
      * select * from integrations.stage_entities where solr_query='{"q":"realm_id:223146344061365 AND
      * (status:TO_BE_REVIEWED AND connection_account_id:8d3f3920-65e0-11e7-be32-d933d5d0997e AND
      * is_primary:true AND transaction_date:[2016-01-01T07:00:00.000Z TO 2018-01-01T07:00:00.000Z])",
      * "start":"0","route.partition":["223146344061365"]}';
      * *
      * select * from integrations.stage_entities where solr_query='{"q":"realm_id:223146344061365
      * AND -(status:ACCEPTED_ADDED AND -(status:ACCEPTED_MATCHED) AND -(status:EXCLUDED) AND
      * connection_account_id:8d3f3920-65e0-11e7-be32-d933d5d0997e)","start":"0","route.partition":["223146344061365"]}';
      */

    val solrQueries = ListBuffer[String]()

    val connectionAccountId = "2f6778f5-a3c3-11e7-96aa-01ae833a4ee".concat(faker.number.numberBetween(1, 3).toString)

    solrQueries.append(

      // select * from integrations.stage_entities where solr_query='{"q":"realm_id:223146344061365 AND
      // status:TO_BE_REVIEWED","start":"0","route.partition":["223146344061365"]}';
      new SolrQueryBuilder()
          .addFilterQuery(s"realm_id:$realmId", cached = false)
          .addFilterQuery("status:TO_BE_REVIEWED")
          .addRoutePartition(List(realmId))
          .getSolrQuery,


      // select * from integrations.stage_entities where solr_query='{"q":"realm_id:223146344061365 AND
      // (status:TO_BE_REVIEWED AND connection_account_id:8d3f3920-65e0-11e7-be32-d933d5d0997e)","start":"0","route.partition":["223146344061365"]}';
      new SolrQueryBuilder()
          .addFilterQuery(s"realm_id:$realmId", cached = false)
          .addFilterQuery("status:TO_BE_REVIEWED")
          .addQuery(s"connection_account_id:$connectionAccountId")
          .addRoutePartition(List(realmId))
          .getSolrQuery,


      //select * from integrations.stage_entities where solr_query='{"q":"realm_id:223146344061365 AND
      // (status:TO_BE_REVIEWED AND connection_account_id:8d3f3920-65e0-11e7-be32-d933d5d0997e AND
      // transaction_date:[2016-01-01T07:00:00.000Z TO 2018-01-01T07:00:00.000Z])","start":"0","route.partition":["223146344061365"]}';
      new SolrQueryBuilder()
          .addFilterQuery(s"realm_id:$realmId", cached = false)
          .addFilterQuery("status:TO_BE_REVIEWED")
          .addQuery(s"connection_account_id:$connectionAccountId AND " +
              s"transaction_date:[2016-01-01T00:00:00.000Z TO 2016-12-31T23:59:59.999Z] AND deleted:false")
          .addRoutePartition(List(realmId))
          .getSolrQuery,

      new SolrQueryBuilder()
          .addFilterQuery(s"realm_id:$realmId", cached = false)
          .addFilterQuery("status:TO_BE_REVIEWED")
          .addQuery(s"connection_account_id:$connectionAccountId AND " +
              s"transaction_date:[2017-01-01T00:00:00.000Z TO 2017-12-31T23:59:59.999Z] AND deleted:false")
          .addRoutePartition(List(realmId))
          .getSolrQuery,


      //select * from integrations.stage_entities where solr_query='{"q":"realm_id:223146344061365 AND
      // (status:TO_BE_REVIEWED AND connection_account_id:8d3f3920-65e0-11e7-be32-d933d5d0997e AND
      // is_primary:true AND transaction_date:[2016-01-01T07:00:00.000Z TO 2018-01-01T07:00:00.000Z])",
      // "start":"0","route.partition":["223146344061365"]}';
      new SolrQueryBuilder()
          .addFilterQuery(s"realm_id:$realmId", cached = false)
          .addFilterQuery("status:TO_BE_REVIEWED")
          .addQuery(s"connection_account_id:$connectionAccountId AND " +
              s"transaction_date:[2016-01-01T00:00:00.000Z TO 2016-12-31T23:59:59.999Z] AND is_primary:true AND deleted:false")
          .addRoutePartition(List(realmId))
          .getSolrQuery,

      new SolrQueryBuilder()
          .addFilterQuery(s"realm_id:$realmId", cached = false)
          .addFilterQuery("status:TO_BE_REVIEWED")
          .addQuery(s"connection_account_id:$connectionAccountId AND " +
              s"transaction_date:[2017-01-01T00:00:00.000Z TO 2017-12-31T23:59:59.999Z] AND is_primary:true AND deleted:false")
          .addRoutePartition(List(realmId))
          .getSolrQuery,


      // select * from integrations.stage_entities where solr_query='{"q":"realm_id:223146344061365
      // AND -(status:ACCEPTED_ADDED AND -(status:ACCEPTED_MATCHED) AND -(status:EXCLUDED) AND
      // connection_account_id:8d3f3920-65e0-11e7-be32-d933d5d0997e)","start":"0","route.partition":["223146344061365"]}';
      new SolrQueryBuilder()
          .addFilterQuery(s"realm_id:$realmId", cached = false)
          .addFilterQuery("status:TO_BE_REVIEWED")
          .addQuery(s"-status:(ACCEPTED_ADDED OR ACCEPTED_MATCHED OR EXCLUDED) AND " +
              s"connection_account_id:$connectionAccountId")
          .addRoutePartition(List(realmId))
          .getSolrQuery

    )

    val query = getRandom(solrQueries.toList)
      logger.warn(query)
    query
  }

}

