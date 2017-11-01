package actions.intuit

import java.io.InputStream

import com.datastax.driver.core.{ConsistencyLevel, DataType}
import com.datastax.driver.core.querybuilder.QueryBuilder._
import com.datastax.driver.core.querybuilder.{Insert, QueryBuilder}
import com.datastax.driver.core.schemabuilder.SchemaBuilder
import com.datastax.driver.core.schemabuilder.SchemaBuilder.Direction
import com.datastax.gatling.plugin.CqlPredef._
import com.datastax.gatling.stress.core.BaseAction
import com.datastax.gatling.stress.libs.{Cassandra, SimConfig}
import com.mashape.unirest.http.Unirest
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import org.json4s.DefaultFormats


/**
  * Member Actions
  *
  * @param cassandra Cassandra
  * @param simConf   SimConfig
  */
class TripActions(cassandra: Cassandra, simConf: SimConfig) extends BaseAction(cassandra, simConf) {

  object tables {
    val vehicle = "vehicle"
    val trip = "trip"
    val vehicleAnnualSummary = "vehicle_annual_summary"
  }

  implicit val formats = DefaultFormats

  createKeyspace
  createTables
//  createSolrSchema

  private val writeTripQuery: Insert = QueryBuilder.insertInto(keyspace, tables.trip)
      .value("realm_id", raw(":realm_id")) // bigint
      .value("trip_id", raw(":trip_id")) // timeuuid
      .value("auto_tracked", raw(":auto_tracked")) // boolean
      .value("created_by_user", raw(":created_by_user")) // bigint
      .value("date_created", raw(":date_created")) // timestamp
      .value("date_updated", raw(":date_updated")) // timestamp
      .value("deleted", raw(":deleted")) // boolean
      .value("description", raw(":description")) // text
      .value("device_type", raw(":device_type")) // varchar
      .value("distance", raw(":distance")) // decimal
      .value("distance_unit", raw(":distance_unit")) // varchar
      .value("end_address_city", raw(":end_address_city")) // text
      .value("end_address_country", raw(":end_address_country")) // text
      .value("end_address_line1", raw(":end_address_line1")) // text
      .value("end_address_line2", raw(":end_address_line2")) // text
      .value("end_address_postal_code", raw(":end_address_postal_code")) // text
      .value("end_address_state", raw(":end_address_state")) // text
      .value("end_datetime", raw(":end_datetime")) // timestamp
      .value("end_datetime_offset", raw(":end_datetime_offset")) // text
      .value("end_latitude", raw(":end_latitude")) // decimal
      .value("end_location", raw(":end_location")) // text
      .value("end_location_id", raw(":end_location_id")) // timeuuid
      .value("end_longitude", raw(":end_longitude")) // decimal
      .value("external_id", raw(":external_id")) // varchar
      .value("import_source", raw(":import_source")) // varchar
      .value("notes", raw(":notes")) // text
      .value("review_state", raw(":review_state")) // varchar
      .value("start_address_city", raw(":start_address_city")) // text
      .value("start_address_country", raw(":start_address_country")) // text
      .value("start_address_line1", raw(":start_address_line1")) // text
      .value("start_address_line2", raw(":start_address_line2")) // text
      .value("start_address_postal_code", raw(":start_address_postal_code")) // text
      .value("start_address_state", raw(":start_address_state")) // text
      .value("start_datetime", raw(":start_datetime")) // timestamp
      .value("start_datetime_offset", raw(":start_datetime_offset")) // text
      .value("start_latitude", raw(":start_latitude")) // decimal
      .value("start_location", raw(":start_location")) // text
      .value("start_location_id", raw(":start_location_id")) // timeuuid
      .value("start_longitude", raw(":start_longitude")) // decimal
      .value("updated_by_user", raw(":updated_by_user")) // bigint
      .value("user_agent", raw(":user_agent")) // text
      .value("user_id", raw(":user_id")) // bigint
      .value("vehicle_id", raw(":vehicle_id")) // timeuuid


  private val writeVehicleQuery: Insert = QueryBuilder.insertInto(keyspace, tables.vehicle)
      .value("realm_id", raw(":realm_id")) // bigint
      .value("vehicle_id", raw(":vehicle_id")) // timeuuid
      .value("created_by_user", raw(":created_by_user")) // bigint
      .value("date_acquired", raw(":date_acquired")) // timestamp
      .value("date_created", raw(":date_created")) // timestamp
      .value("date_in_service", raw(":date_in_service")) // timestamp
      .value("date_updated", raw(":date_updated")) // timestamp
      .value("deleted", raw(":deleted")) // boolean
      .value("description", raw(":description")) // text
      .value("external_id", raw(":external_id")) // varchar
      .value("fair_market_value", raw(":fair_market_value")) // decimal
      .value("is_default", raw(":is_default")) // boolean
      .value("leased", raw(":leased")) // boolean
      .value("purchase_price", raw(":purchase_price")) // decimal
      .value("updated_by_user", raw(":updated_by_user")) // bigint
      .value("user_id", raw(":user_id")) // bigint
      .value("vehicle_type", raw(":vehicle_type")) // varchar
      .value("vehicle_year", raw(":vehicle_year")) // int


  private val solrQuery = QueryBuilder.select().from(keyspace, tables.trip)
      .where(QueryBuilder.eq("solr_query", raw(":solr_query")))


  def writeTrip: ChainBuilder = {
    val writeTripsPStatement = session.prepare(writeTripQuery)

    exec(
      cql("InsertTrip")
          .executeNamed(writeTripsPStatement)
    )
  }

  def writeVehicle: ChainBuilder = {
    val writeVehiclePStatement = session.prepare(writeVehicleQuery)

    exec(
      cql("InsertVehicle")
          .executeNamed(writeVehiclePStatement)
    )
  }


  def queryWithSolr = {
    val solrQueryPreparedStatement = session.prepare(solrQuery)

    exec(
      cql("SolrQuery")
          .executeNamed(solrQueryPreparedStatement)
          .consistencyLevel(ConsistencyLevel.LOCAL_ONE)
          .check(rowCount greaterThan 0)
    )
  }


  private def createTables = {

    runQueries(Array(

      SchemaBuilder.createTable(keyspace, tables.vehicle)
          .addPartitionKey("realm_id", DataType.bigint())
          .addClusteringColumn("vehicle_id", DataType.timeuuid())
          .addColumn("created_by_user", DataType.bigint())
          .addColumn("date_acquired", DataType.timestamp())
          .addColumn("date_created", DataType.timestamp())
          .addColumn("date_in_service", DataType.timestamp())
          .addColumn("date_updated", DataType.timestamp())
          .addColumn("deleted", DataType.cboolean())
          .addColumn("description", DataType.text())
          .addColumn("external_id", DataType.varchar())
          .addColumn("fair_market_value", DataType.decimal())
          .addColumn("is_default", DataType.cboolean())
          .addColumn("leased", DataType.cboolean())
          .addColumn("purchase_price", DataType.decimal())
          .addColumn("updated_by_user", DataType.bigint())
          .addColumn("vehicle_type", DataType.varchar())
          .addColumn("vehicle_year", DataType.cint())
          .addColumn("user_id", DataType.bigint())
          .ifNotExists()
          .withOptions()
          .clusteringOrder("vehicle_id", Direction.DESC)
          .toString,



      SchemaBuilder.createTable(keyspace, tables.trip)
          .addPartitionKey("realm_id", DataType.bigint())
          .addClusteringColumn("trip_id", DataType.timeuuid())
          .addColumn("auto_tracked", DataType.cboolean())
          .addColumn("created_by_user", DataType.bigint())
          .addColumn("date_created", DataType.timestamp())
          .addColumn("date_updated", DataType.timestamp())
          .addColumn("deleted", DataType.cboolean())
          .addColumn("description", DataType.text())
          .addColumn("device_type", DataType.varchar())
          .addColumn("distance", DataType.decimal())
          .addColumn("distance_unit", DataType.varchar())

          .addColumn("end_address_city", DataType.text())
          .addColumn("end_address_country", DataType.text())
          .addColumn("end_address_line1", DataType.text())
          .addColumn("end_address_line2", DataType.text())
          .addColumn("end_address_postal_code", DataType.text())
          .addColumn("end_address_state", DataType.text())
          .addColumn("end_datetime", DataType.timestamp())
          .addColumn("end_datetime_offset", DataType.text())
          .addColumn("end_latitude", DataType.decimal())
          .addColumn("end_location", DataType.text())
          .addColumn("end_location_id", DataType.timeuuid())
          .addColumn("end_longitude", DataType.decimal())

          .addColumn("external_id", DataType.varchar())
          .addColumn("import_source", DataType.varchar())
          .addColumn("notes", DataType.text())
          .addColumn("review_state", DataType.varchar())

          .addColumn("start_address_city", DataType.text())
          .addColumn("start_address_country", DataType.text())
          .addColumn("start_address_line1", DataType.text())
          .addColumn("start_address_line2", DataType.text())
          .addColumn("start_address_postal_code", DataType.text())
          .addColumn("start_address_state", DataType.text())
          .addColumn("start_datetime", DataType.timestamp())
          .addColumn("start_datetime_offset", DataType.text())
          .addColumn("start_latitude", DataType.decimal())
          .addColumn("start_location", DataType.text())
          .addColumn("start_location_id", DataType.timeuuid())
          .addColumn("start_longitude", DataType.decimal())

          .addColumn("updated_by_user", DataType.bigint())
          .addColumn("user_agent", DataType.text())
          .addColumn("user_id", DataType.bigint())
          .addColumn("vehicle_id", DataType.timeuuid())

          .ifNotExists()
          .withOptions()
          .clusteringOrder("trip_id", Direction.DESC)
          .toString,


      SchemaBuilder.createTable(keyspace, tables.vehicleAnnualSummary)
          .addPartitionKey("realm_id", DataType.bigint())
          .addClusteringColumn("vehicle_id", DataType.timeuuid())
          .addClusteringColumn("year", DataType.cint())
          .addColumn("odometer_end", DataType.cint())
          .addColumn("odometer_end", DataType.cint())
          .addColumn("odometer_start", DataType.bigint())
          .addColumn("total_business_distance", DataType.decimal())
          .addColumn("total_business_distance_unit", DataType.text())
          .addColumn("total_distance", DataType.decimal())
          .addColumn("total_distance_unit", DataType.text())

          .ifNotExists()
          .withOptions()
          .clusteringOrder("vehicle_id", Direction.DESC)
          .clusteringOrder("year", Direction.DESC)
          .toString

    ))

  }


  private def createSolrSchema = {

    val solrConfig = simConf.getSimulationConf.getConfig("solr")
    val httpsBool = solrConfig.getBoolean("https")

    val urlBase = StringBuilder.newBuilder
    if (httpsBool) {
      urlBase.append("https")
    } else {
      urlBase.append("http")
    }

    urlBase.append("://")
    urlBase.append(simConf.getCassandraConf.getList("hosts").unwrapped().get(0))
    urlBase.append(":")
    urlBase.append(solrConfig.getInt("httpPort"))

    // URL: /solr/admin/cores?action=CREATE&name=$keyspace.$table
    val createCoreBase = urlBase.clone()
    createCoreBase.append("/solr/admin/cores")

    // URL: http://localhost:8983/solr/resource/keyspace.table/solrconfig.xml
    val createResourceBase = urlBase.clone()
    createResourceBase.append(s"/solr/resource/$keyspace.$table/")

    val stream_1 = getClass.getResourceAsStream(solrConfig.getString("configXml"))
    val solrConfigXml = scala.io.Source.fromInputStream(stream_1).mkString

    val stream_2: InputStream = getClass.getResourceAsStream(solrConfig.getString("schemaXml"))
    val solrSchemaXml = scala.io.Source.fromInputStream(stream_2).mkString

    val submitSolrConfigResource = Unirest.post(createResourceBase.toString() + "solrconfig.xml")
        .header("Content-type", "text/xml; charset=utf-8")
        .body(solrConfigXml)
        .asBinary()

    if (submitSolrConfigResource.getStatus != 200) {
      logger.error(s"Unable to submit solrconfig.xml file for core $keyspace.$table. " +
          s"Server Error: ${submitSolrConfigResource.getBody}")
      System.exit(1)
    }

    val submitSolrSchemaResource = Unirest.post(createResourceBase.toString() + "schema.xml")
        .header("Content-type", "text/xml; charset=utf-8")
        .body(solrSchemaXml)
        .asBinary()

    if (submitSolrSchemaResource.getStatus != 200) {
      logger.error(s"Unable to submit schema.xml file for core $keyspace.$table. " +
          s"Server Error: ${submitSolrSchemaResource.getBody}")
      System.exit(1)
    }

    val createCoreResponse = Unirest.post(createCoreBase.toString())
        .queryString("action", "CREATE")
        .queryString("name", s"$keyspace.$table")
        .queryString("reindex", "false")
        .asString()

    if (createCoreResponse.getStatus != 200) {

      if (createCoreResponse.getBody.contains("already exists and is loaded")) {
        logger.warn(s"Core for $keyspace.$table is already created, skipping.")
      } else {
        logger.error(s"Unable to submit CREATE core for $keyspace.$table. " +
            s"erver Error: ${createCoreResponse.getBody}")
        System.exit(1)
      }

    }


  }
}

