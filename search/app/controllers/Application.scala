package controllers

import models.{Searcher, SearchParams}
import play.api.data.Forms.{text, number, mapping}
import play.api.data.Form
import play.api.libs.json.{Json, JsValue}
import play.api.libs.ws.WS
import play.api.mvc.{Controller, Action}
import play.api.Play

object Application extends Controller {

  // define the search form
  val searchForm = Form(
    mapping (
      "index" -> text,
      "query" -> text,
      "filter" -> text,
      "start" -> number,
      "rows" -> number,
      "sort" -> text,
      "writertype" -> text,
      "fieldlist" -> text,
      "highlightfields" -> text,
      "facetfields" -> text
    ) (SearchParams.apply)(SearchParams.unapply)
  )
  
  // configuration parameters from conf/application.conf
  val conf = Play.current.configuration
  val server = conf.getString("es.server").get

  // home page - redirects to search form
  def index = Action {
    Redirect(routes.Application.form)
  }

  // form page
  def form = Action {
    val rsp = Json.parse(WS.url(server + "_status").
      get.value.get.body)
    val indices = ((rsp \\ "indices")).
      map(_.as[Map[String,JsValue]].keySet.head)
    Ok(views.html.index(indices, searchForm))
  } 

  // search results action - can send view to one of
  // three different pages (xmlSearch, jsonSearch or htmlSearch)
  // depending on value of writertype
  def search = Action {request =>
    val params = request.queryString.
      map(elem => elem._1 -> elem._2.headOption.getOrElse(""))
    val searchParams = searchForm.bind(params).get
    val result = Searcher.search(server, searchParams)
    searchParams.writertype match {
      case "json" => Ok(result.raw).as("text/javascript")
      case "html" => Ok(views.html.search(result)).as("text/html")
    }
  }
}