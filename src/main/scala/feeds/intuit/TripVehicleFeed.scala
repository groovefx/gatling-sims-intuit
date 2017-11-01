package feeds.intuit

import com.datastax.gatling.stress.core.BaseFeed
import com.datastax.gatling.stress.libs.SimConfig
import com.typesafe.scalalogging.LazyLogging


class TripVehicleFeed(conf: SimConfig) extends BaseFeed with LazyLogging {

  val tripCount100RowsPercent = conf.getSimulationConfDouble("tripCnt100RowsPercent")
  val tripCount250RowsPercent = conf.getSimulationConfDouble("tripCnt250RowsPercent")
  val tripCount500RowsPercent = conf.getSimulationConfDouble("tripCnt500RowsPercent")
  val tripCount1000RowsPercent = conf.getSimulationConfDouble("tripCnt1000RowsPercent")

  val tripCount = new WeightedRandomGenerator(List(
    (100, tripCount100RowsPercent),
    (250, tripCount250RowsPercent),
    (500, tripCount500RowsPercent),
    (1000, tripCount1000RowsPercent)
  ))

  val vehicleCount = new WeightedRandomGenerator(List((5, 20.0), (1, 80.0)))
  val autoTracked = new WeightedRandomGenerator(List((true, 80.0), (false, 20.0)))

  def writeRealm = Iterator.continually(getRealmData)
  def writeTrip = Iterator.continually(getTripData)
  def writeVehicle = Iterator.continually(getVehicleData)

  private def getRealmData = {

    val user = faker.number.digits(10).toLong

    Map(
      "realm_id" -> faker.number.digits(15).toLong, // bigint  123145804260649
      "vehicle_cnt" -> vehicleCount.getItem,
      "trip_cnt" -> tripCount.getItem,
      "created_by_user" -> user, // bigint
      "updated_by_user" -> user, // bigint
      "user_id" -> user // bigint
    )
  }


  private def getVehicleData = {

    Map(
      "vehicle_id" -> getTimeUuid, // timeuuid
      "date_acquired" -> getRandomTimestamp("2010-01-01 00:00:00", "2017-09-01 00:00:00"), // timestamp
      "date_created" -> getCurrentTimestamp, // timestamp
      "date_in_service" -> getRandomTimestamp("2017-01-01 00:00:00", "2017-09-01 00:00:00"), // timestamp
      "date_updated" -> getCurrentTimestamp, // timestamp
      "deleted" -> false, // boolean
      "description" -> faker.gameOfThrones.quote, // text
      "external_id" -> faker.number.digits(12), // varchar
      "fair_market_value" -> faker.commerce.price(500.00, 50000.00).toDouble, // decimal
      "is_default" -> true, // boolean
      "leased" -> faker.bool.bool, // boolean
      "purchase_price" -> faker.commerce.price(15000.00, 50000.00).toDouble, // decimal
      "vehicle_type" -> getRandom(Array("CAR_TRUCK", "MOTORCYCLE", "BICYCLE")), // varchar
      "vehicle_year" -> faker.number.numberBetween(2010, 2017) // int
    )
  }


  private def getTripData = {

    val offset = faker.bothify("-0#:00") // text

    Map(
      "trip_id" -> getTimeUuid, // timeuuid
      "auto_tracked" -> autoTracked.getItem, // boolean
      "date_created" -> getCurrentTimestamp, // timestamp
      "date_updated" -> getCurrentTimestamp, // timestamp
      "deleted" -> false, // boolean
      "description" -> faker.chuckNorris.fact(), // text
      "device_type" -> getRandom(Array("iOS", "Android", "Web")), // varchar
      "distance" -> faker.number.randomDouble(2, 1, 50), // decimal
      "distance_unit" -> "MILES", // varchar
      "end_address_city" -> faker.address.city, // text
      "end_address_country" -> "US", // text
      "end_address_line1" -> faker.address.streetAddress(), // text
      "end_address_line2" -> faker.address.secondaryAddress(), // text
      "end_address_postal_code" -> faker.address.zipCode, // text
      "end_address_state" -> faker.address.state, // text
      "end_datetime" -> getCurrentTimestamp, // timestamp
      "end_datetime_offset" -> offset, // text
      "end_latitude" -> faker.address.latitude.toDouble, // decimal
      "end_location" -> faker.bothify("?????####????"), // text
      "end_location_id" -> getTimeUuid, // timeuuid
      "end_longitude" -> faker.address.longitude.toDouble, // decimal
      "external_id" -> faker.number.digits(12), // varchar
      "import_source" -> faker.bothify("????????"), // varchar
      "notes" -> getRandom(Array(faker.shakespeare.hamletQuote, faker.shakespeare.asYouLikeItQuote(),
        faker.shakespeare.kingRichardIIIQuote, faker.shakespeare.romeoAndJulietQuote, "", "")), // text
      "review_state" -> getRandom(Array("PERSONAL", "BUSINESS", "UNREVIEWED")), // varchar
      "start_address_city" -> faker.address.streetAddress(), // text
      "start_address_country" -> "US", // text
      "start_address_line1" -> faker.address.streetAddress(), // text
      "start_address_line2" -> faker.address.secondaryAddress(), // text
      "start_address_postal_code" -> faker.address.zipCode, // text
      "start_address_state" -> faker.address.state, // text
      "start_datetime" -> getRandom(Array(
        getRandomTimestamp("2017-09-20 00:00:00", "2017-09-20 00:00:00"),
        getRandomTimestamp("2016-01-01 00:00:00", "2016-12-31 00:00:00")
      )), // timestamp
      "start_datetime_offset" -> offset, // text
      "start_latitude" -> faker.address.latitude.toDouble, // decimal
      "start_location" -> faker.bothify("?????####????"), // text
      "start_location_id" ->  getTimeUuid, // timeuuid
      "start_longitude" -> faker.address.latitude.toDouble, // decimal
      "user_agent" -> "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.91 Safari/537.36" // text

      //"vehicle_id" -> None, // timeuuid

    )
  }

  def getRandomFromSeq(seq: Seq[Any]): Any = {
    getRandom(seq)
  }


}