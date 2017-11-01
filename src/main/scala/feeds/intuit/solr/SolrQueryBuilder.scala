package feeds.intuit.solr

import com.datastax.gatling.stress.core.BaseFeed
import org.json4s.{JArray, JObject, JString}

import scala.collection.mutable.ListBuffer


class SolrQueryBuilder extends BaseFeed {

  private val fqs = ListBuffer[String]()

  private val q: StringBuilder = new StringBuilder

  private var start: Option[Int] = None

  private var sort: Option[String] = None

  private var routePartition: Option[List[String]] = None


  def addFilterQuery(query: String, cached: Boolean = true) = {
    val fqString = new StringBuilder
    if (!cached) {
      fqString.append("{!cached=false}")
    }
    fqs.append(fqString.append(query).toString())
    this
  }


  def addQuery(query: String) = {
    q.append(query)
    this
  }


  def addSort(field: String, order: String) = {
    sort = Some(s"$field $order")
    this
  }


  def addRoutePartition(fields: List[String]) = {
    routePartition = Some(fields)
    this
  }

  //  override implicit val formats = DefaultFormats

  def getSolrQuery = {

    getJsonString(JObject(
      "q" -> JString(if (q.nonEmpty) q.toString() else "*:*"),
      "fq" -> JArray(if (fqs.nonEmpty) fqs.toList.map(JString) else List.empty),
      "sort" -> JString(if (sort.nonEmpty) sort.get else ""),
      "route.partition" -> JArray(if (routePartition.nonEmpty) routePartition.get.map(JString) else List.empty)
    ))

    //
    //    write(
    //      SolrQuery(
    //        q = if (q.nonEmpty) q.toString() else "*:*",
    //        fq = if (fqs.nonEmpty) Some(fqs.toList) else None,
    ////        start = if (start.nonEmpty) start.toString else None,
    //        sort = if (sort.nonEmpty) sort else None,
    //        routePartition =
    //      )
    //    )
  }

  case class SolrQuery(q: String = "*:*",
                       fq: Option[List[String]] = None,
                       sort: Option[String] = None,
                       facet: Option[String] = None,
                       start: Option[String] = None,
                       routePartition: Option[List[String]] = None
                      )

}
