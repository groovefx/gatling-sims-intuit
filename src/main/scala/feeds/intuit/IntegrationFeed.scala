package feeds.intuit

import com.datastax.gatling.stress.core.BaseFeed
import com.datastax.gatling.stress.libs.SimConfig
import com.typesafe.scalalogging.LazyLogging


class IntegrationFeed(conf: SimConfig) extends BaseFeed with LazyLogging {

  private val rowsPerRealm100Percent = conf.getSimulationConfDouble("rowsPerRealm100Percent")
  private val rowsPerRealm250Percent = conf.getSimulationConfDouble("rowsPerRealm250Percent")
  private val rowsPerRealm500Percent = conf.getSimulationConfDouble("rowsPerRealm500Percent")
  private val rowsPerRealm1000Percent = conf.getSimulationConfDouble("rowsPerRealm1000Percent")
  private val rowsPerRealm5000Percent = conf.getSimulationConfDouble("rowsPerRealm5000Percent")

  private val rowsPerRealm = new WeightedRandomGenerator(List(
    (100, rowsPerRealm100Percent),
    (250, rowsPerRealm250Percent),
    (500, rowsPerRealm500Percent),
    (1000, rowsPerRealm1000Percent),
    (5000, rowsPerRealm5000Percent)
  ))

  def writeRealm = Iterator.continually(getRealmData)

  def writeStageEntity = Iterator.continually(getStageEntityData)

  private def getRealmData = {
    Map(
      "realm_id" -> faker.number.digits(15).toLong, // bigint  123145804260649
      "rows_per_realm" -> rowsPerRealm.getItem
    )
  }

  private def getStageEntityData = {

    Map(
      "entity_id" -> getTimeUuid, // timeuuid
      "amount" -> faker.commerce.price(0, 10000.00).toDouble, // decimal

      "applied_rule_ids" -> Set(getTimeUuid.toString), // set<text>
      "audit_session_id" -> getTimeUuid, // timeuuid
      "category_id" -> faker.number.numberBetween(1, 100).toString, // text
      "category_method_id" -> faker.number.numberBetween(1, 100).toString, // text

      "category_name" -> getRandom(Seq("Meals and Entertainment", "Fuel")), // text
      "cleansed_description" -> faker.chuckNorris.fact(), // text
      "connection_account_id" -> "2f6778f5-a3c3-11e7-96aa-01ae833a4ee".concat(faker.number.numberBetween(1, 3).toString), // uuid

      "created_app_id" -> "Intuit.smallbusiness.qboofferingintegrations.integrationsclient", // text
      "created_date" -> getRandom(Array(
        getRandomTimestamp("2017-09-20 00:00:00", "2017-09-20 00:00:00"),
        getRandomTimestamp("2016-01-01 00:00:00", "2016-12-31 00:00:00")
      )), // timestamp
      "created_user_id" -> faker.number.digits(16), // text
      "deleted" -> faker.bool.bool, // boolean
      "entities_out" -> faker.chuckNorris.fact(), // text
      "entity_type" -> "/transactions/Transaction", // text
      "external_request_ref" -> faker.bothify("??###?###?##"), // text
      "external_transaction_id" -> faker.number.digits(10), // text
      "fi_description" -> faker.color.name(), // text
      "fi_transaction_id" -> faker.bothify("FDP-#########"), // text
      "fi_transaction_type" -> getRandom(Seq("CREDIT", "DEBIT")), // text
      "intuit_tid" -> "integrations-gating-test", // text
      "is_posted" -> faker.bool.bool, // boolean
      "is_primary" -> faker.bool.bool, // boolean
      "iso_currency" -> "USD", // text
      "ledger_account_id" -> "WF_CREDIT_1235_COA", // text
      "linked_stage_entities" -> Set(getTimeUuid.toString, getTimeUuid.toString), // set<text>
      "memo" -> getRandom(Array(faker.shakespeare.hamletQuote, faker.shakespeare.asYouLikeItQuote(),
        faker.shakespeare.kingRichardIIIQuote, faker.shakespeare.romeoAndJulietQuote, "", "")), // text
      "merchant_id" -> faker.bothify("merchant-id-####"), // text
      "merchant_name" -> faker.company.name, // text
      "open_balance" -> faker.commerce.price(0, 10000.00).toDouble, // decimal
      "original_payload" -> payload, // text
      "payload" -> payload, // text
      "posted_date" -> getRandom(Array(
        getRandomTimestamp("2017-09-20 00:00:00", "2017-09-20 00:00:00"),
        getRandomTimestamp("2016-01-01 00:00:00", "2016-12-31 00:00:00")
      )), // timestamp
      "receipt_processing_state" -> "SYNC", // text
      "schedule_c" -> "SCHEDULE_C", // text
      "schedule_c_id" -> faker.bothify("SCHEDULE_C_####"), // text
      "schema_version" -> "1.1", // text
      "sic_code" -> faker.bothify("SIC_CODE_####"), // text
      "status" -> getRandom(Seq("ACCEPTED_ADDED", "TO_BE_REVIEWED", "NOT_PROCESSED")), // text
      "suggested_qbo_matches" -> Set(faker.bothify("###"), faker.bothify("###")), // set<text>
      "transaction_date" -> getRandom(Array(
        getRandomTimestamp("2017-09-20 00:00:00", "2017-09-20 00:00:00"),
        getRandomTimestamp("2016-01-01 00:00:00", "2016-12-31 00:00:00")
      )), // timestamp
      "transaction_source" -> "FDP", // text
      "updated_app_id" -> "Intuit.nsmallbusiness.qboofferingintegrations.integrationsclient", // text
      "updated_date" -> getCurrentTimestamp, // timestamp
      "updated_user_id" -> faker.number.digits(20), // text
      "user_marked_duplicate" -> faker.bool.bool, // boolean
    )
  }

  def getRandomFromSeq(seq: Seq[Any]): Any = {
    getRandom(seq)
  }

  private val payload = """{\"$type\":\"/integration/StageEntity\",\"id\":\"djQuMToxMjMxNDYzNDQwNjEzNjk6MDg4YWZjNDczYQ:2bbe7020-a3ad-11e7-9067-6f3ec322fbd0\",\"entityVersion\":\"0\",\"state\":\"TO_BE_REVIEWED\",\"connectionAccount\":{\"id\":\"djQuMToxMjMxNDYzMTI4NjMyNjQ6Y2YxNWI0NDZiMg:8d3f3920-65e0-11e7-be32-d933d5d0997e\"},\"entityIn\":{\"$type\":\"/transactions/Transaction\",\"externalIds\":[{\"namespaceId\":\"paypal.transactionId\",\"localId\":\"7T524118PW532862R\"},{\"namespaceId\":\"paypal.referenceId\",\"localId\":null}],\"customFields\":[{\"bank_reference_id\":null,\"payer_address_status\":\"Y\",\"custom_field\":null,\"invoice_id\":null,\"transaction_event_code\":\"T0006\"}],\"type\":\"PURCHASE\",\"header\":{\"txnDate\":\"2017-06-20\",\"referenceNumber\":\"1FB671465F828410U\",\"privateMemo\":\"PaidforStationery\",\"amount\":\"56.31\",\"currencyInfo\":{\"currency\":\"USD\"},\"contact\":{\"contactMethods\":[{\"primaryEmail\":{\"emailAddress\":\"nbrungi+merchant3@gmail.com\"},\"addresses\":[{\"freeFormAddressLine\":null}]}],\"displayName\":\"STAPLES\",\"givenName\":\"QBOStage\",\"familyName\":\"TxnsOne\"}},\"traits\":{\"tax\":{\"totalTaxAmount\":\"0.00\",\"totalTaxPercent\":{\"percent\":false,\"moneyValue\":\"0.00\"}},\"shipping\":{\"shipName\":\"Test1 Test2\",\"shipAddress\":{\"freeFormAddressLine\":\"1 Main StSan Jose US95131\"},\"shippingAmount\":\"0.00\"}},\"lines\":{\"itemLines\":[{\"description\":\"Pens, Pencils,Folders\",\"amount\":\"40.00\",\"item\":{\"$type\":\"/transactions/Line#/definitions/ItemTrait\",\"quantity\":\"2\",\"rate\":{\"percent\":false,\"moneyValue\":\"20.00\"},\"customFields\":[{\"$type\":\"/common/CustomFieldValue\"}],\"name\":\"Stationery\",\"sku\":null},\"tax\":{\"$ref\":\"#/entityIn/traits/tax\"}}]}},\"transactionTrait\":{\"transaction\":{\"$ref\":\"#/entityIn\"}}}"""


}