package actions.intuit

import com.datastax.driver.core.ConsistencyLevel
import com.datastax.driver.core.querybuilder.QueryBuilder._
import com.datastax.driver.core.querybuilder.{Insert, QueryBuilder}
import com.datastax.gatling.plugin.CqlPredef._
import com.datastax.gatling.stress.core.BaseAction
import com.datastax.gatling.stress.libs.{Cassandra, SimConfig}
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import org.json4s.DefaultFormats


/**
  * Member Actions
  *
  * @param cassandra Cassandra
  * @param simConf   SimConfig
  */
class IntegrationActions(cassandra: Cassandra, simConf: SimConfig) extends BaseAction(cassandra, simConf) {

  object tables {
    val stageEntities = "stage_entities"
  }

  implicit val formats = DefaultFormats

  createKeyspace
  createTables
  //  createSolrSchema

  private val writeStageEntitiesQuery: Insert = QueryBuilder.insertInto(keyspace, tables.stageEntities)
      .value("realm_id", raw(":realm_id")) 	// bigint
      .value("entity_id", raw(":entity_id")) 	// timeuuid
      .value("amount", raw(":amount")) 	// decimal
      .value("applied_rule_ids", raw(":applied_rule_ids")) 	// set<text>
      .value("audit_session_id", raw(":audit_session_id")) 	// timeuuid
      .value("category_id", raw(":category_id")) 	// text
      .value("category_method_id", raw(":category_method_id")) 	// text
      .value("category_name", raw(":category_name")) 	// text
      .value("cleansed_description", raw(":cleansed_description")) 	// text
      .value("connection_account_id", raw(":connection_account_id")) 	// uuid
      .value("created_app_id", raw(":created_app_id")) 	// text
      .value("created_date", raw(":created_date")) 	// timestamp
      .value("created_user_id", raw(":created_user_id")) 	// text
      .value("deleted", raw(":deleted")) 	// boolean
      .value("entities_out", raw(":entities_out")) 	// text
      .value("entity_type", raw(":entity_type")) 	// text
      .value("external_request_ref", raw(":external_request_ref")) 	// text
      .value("external_transaction_id", raw(":external_transaction_id")) 	// text
      .value("fi_description", raw(":fi_description")) 	// text
      .value("fi_transaction_id", raw(":fi_transaction_id")) 	// text
      .value("fi_transaction_type", raw(":fi_transaction_type")) 	// text
      .value("intuit_tid", raw(":intuit_tid")) 	// text
      .value("is_posted", raw(":is_posted")) 	// boolean
      .value("is_primary", raw(":is_primary")) 	// boolean
      .value("iso_currency", raw(":iso_currency")) 	// text
      .value("ledger_account_id", raw(":ledger_account_id")) 	// text
      .value("linked_stage_entities", raw(":linked_stage_entities")) 	// set<text>
      .value("memo", raw(":memo")) 	// text
      .value("merchant_id", raw(":merchant_id")) 	// text
      .value("merchant_name", raw(":merchant_name")) 	// text
      .value("open_balance", raw(":open_balance")) 	// decimal
      .value("original_payload", raw(":original_payload")) 	// text
      .value("payload", raw(":payload")) 	// text
      .value("posted_date", raw(":posted_date")) 	// timestamp
      .value("receipt_processing_state", raw(":receipt_processing_state")) 	// text
      .value("schedule_c", raw(":schedule_c")) 	// text
      .value("schedule_c_id", raw(":schedule_c_id")) 	// text
      .value("schema_version", raw(":schema_version")) 	// text
      .value("sic_code", raw(":sic_code")) 	// text
      .value("status", raw(":status")) 	// text
      .value("suggested_qbo_matches", raw(":suggested_qbo_matches")) 	// set<text>
      .value("transaction_date", raw(":transaction_date")) 	// timestamp
      .value("transaction_source", raw(":transaction_source")) 	// text
      .value("updated_app_id", raw(":updated_app_id")) 	// text
      .value("updated_date", raw(":updated_date")) 	// timestamp
      .value("updated_user_id", raw(":updated_user_id")) 	// text
      .value("user_marked_duplicate", raw(":user_marked_duplicate")) 	// boolean


  private val solrQuery = QueryBuilder.select().from(keyspace, tables.stageEntities)
      .where(QueryBuilder.eq("solr_query", raw(":solr_query")))


  def writeStageEntity: ChainBuilder = {
    val writeStageEntitiesPStatement = session.prepare(writeStageEntitiesQuery)

    exec(
      cql("InsertStageEntity")
          .executeNamed(writeStageEntitiesPStatement)
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

    exec(session => {logger.info("solr_query  : " + session("responsePayload")
    )
      session
    })
  }


  private def createTables = {
    runQueries(Array(

      s"""CREATE TABLE IF NOT EXISTS $keyspace.${tables.stageEntities} (
         |    realm_id bigint,
         |    entity_id timeuuid,
         |    amount decimal,
         |    applied_rule_ids set<text>,
         |    audit_session_id timeuuid,
         |    category_id text,
         |    category_method_id text,
         |    category_name text,
         |    cleansed_description text,
         |    connection_account_id uuid,
         |    created_app_id text,
         |    created_date timestamp,
         |    created_user_id text,
         |    deleted boolean,
         |    entities_out text,
         |    entity_type text,
         |    external_request_ref text,
         |    external_transaction_id text,
         |    fi_description text,
         |    fi_transaction_id text,
         |    fi_transaction_type text,
         |    intuit_tid text,
         |    is_posted boolean,
         |    is_primary boolean,
         |    iso_currency text,
         |    ledger_account_id text,
         |    linked_stage_entities set<text>,
         |    memo text,
         |    merchant_id text,
         |    merchant_name text,
         |    open_balance decimal,
         |    original_payload text,
         |    payload text,
         |    posted_date timestamp,
         |    receipt_processing_state text,
         |    schedule_c text,
         |    schedule_c_id text,
         |    schema_version text,
         |    sic_code text,
         |    status text,
         |    suggested_qbo_matches set<text>,
         |    transaction_date timestamp,
         |    transaction_source text,
         |    updated_app_id text,
         |    updated_date timestamp,
         |    updated_user_id text,
         |    user_marked_duplicate boolean,
         |    PRIMARY KEY (realm_id, entity_id)
         |) WITH CLUSTERING ORDER BY (entity_id DESC);""".stripMargin
    ))

  }

}

