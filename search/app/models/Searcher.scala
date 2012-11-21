package models

import scala.Array.canBuildFrom

import play.api.libs.json.{Json, JsValue}
import play.api.libs.ws.WS

case class SearchResult(
  meta: Map[String,Any], 
  docs: Seq[Seq[(String,JsValue)]],
  raw: String
)

case class SearchParams(
  index: String,
  query: String,
  filter: String,
  start: Int,
  rows: Int,
  sort: String,
  writertype: String, 
  fieldlist: String,
  highlightfields: String,
  facetfields: String
)

object Searcher {
  
  def search(server: String, params: SearchParams): SearchResult = {
    val payload = Searcher.buildQuery(params)
    val rawResponse = WS.url(server + params.index + 
      "/_search?pretty=true").post(payload).value.get.body
    val rsp = Json.parse(rawResponse)
    val meta = (rsp \ "error").asOpt[String] match {
      case Some(x) => Map(
        "error" -> x,
        "status" -> (rsp \ "status").asOpt[Int].get
      )
      case None => Map(
        "QTime" -> (rsp \ "took").asOpt[Int].get,
        "start" -> params.start,
        "end" -> (params.start + params.rows),
        "query_json" -> payload,
        "numFound" -> (rsp \ "hits" \ "total").asOpt[Int].get,
        // maxScore == null when sort is selected
        "maxScore" -> (rsp \ "hits" \ "max_score").asOpt[Float].getOrElse(0.0F)
      )
    }
    val docs = if (meta.contains("error")) Seq()
    else {
      val hits = (rsp \ "hits" \ "hits").asOpt[List[JsValue]].get
      val idscores = hits.map(hit => Map(
        "_id" -> (hit \ "_id"),
        "_score" -> (hit \ "_score")))
      val fields = hits.map(hit => 
        (hit \ "fields").asOpt[Map[String,JsValue]].get)
      idscores.zip(fields).
        map(tuple => tuple._1 ++ tuple._2).
        map(doc => doc.toSeq.sortWith((doc1, doc2) => doc1._1 < doc2._1))
    }
    new SearchResult(meta, docs, rawResponse)
  }
  
  def buildQuery(params: SearchParams): String = {
    val queryQuery = Json.toJson(
      if (params.query.isEmpty || "*:*".equals(params.query))
        Map("match_all" -> Map.empty[String,String])
      else Map("query_string" -> Map("query" -> params.query)))
    val queryFilter = if (params.filter.isEmpty) null
      else Json.toJson(Map("query_string" -> Json.toJson(params.filter)))
    val queryFacets = if (params.facetfields.isEmpty) null
      else {
        val fields = params.facetfields.split(",").map(_.trim)
        Json.toJson(fields.zip(fields.
          map(field => Map("terms" -> Map("field" -> field)))).toMap)
      }
    val querySort = if (params.sort.isEmpty) null
      else Json.toJson(params.sort.split(",").map(_.trim).map(field => 
        if (field.toLowerCase.endsWith(" asc") || 
            field.toLowerCase.endsWith(" desc")) 
          (field.split(" ")(0), field.split(" ")(1)) 
        else (field, "")).map(tuple => 
          if (tuple._2.isEmpty) Json.toJson(tuple._1)
          else Json.toJson(Map(tuple._1 -> tuple._2))))  
    val queryFields = Json.toJson(
      if (params.fieldlist.isEmpty) Array("*")
      else params.fieldlist.split(",").map(_.trim))
    val queryHighlight = if (params.highlightfields.isEmpty) null
      else {
        val fields = params.highlightfields.split(",").map(_.trim)
        Json.toJson(Map("fields" -> fields.zip(fields.
          map(field => Map.empty[String,String])).toMap))
      }
    Json.stringify(Json.toJson(Map(
      "from" -> Json.toJson(params.start),
      "size" -> Json.toJson(params.rows),
      "query" -> queryQuery,
      "filter" -> queryFilter,
      "facets" -> queryFacets,
      "sort" -> querySort,
      "fields" -> queryFields,
      "highlight" -> queryHighlight).
      filter(tuple => tuple._2 != null)))
  }
}
