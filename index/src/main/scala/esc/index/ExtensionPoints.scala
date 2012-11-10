package esc.index

import scala.io.Source
import play.api.libs.json.Json


/////////////// Parser /////////////////

/**
 * An implementation of the Parser trait must be supplied
 * by the user for each new data source. The parse() method
 * defines the parsing logic for the new content.
 */
trait Parser {
  
  /**
   * @param s a Source representing a file on local filesystem.
   * @return a Map of field name and value.
   */
  def parse(s: Source): Map[String,String]
}

/////////////// Schema /////////////////

/**
 * An implementation of the Schema trait must be supplied 
 * by the user for each new data source. The mappings() 
 * method is a JSON string containing the fields and their
 * properties. It can be used to directly do a put_mapping
 * call on elastic search. The base trait defines some 
 * convenience methods on the mapping string.
 */
trait Schema {
  
  /**
   * @return a JSON string representing the field names
   * and properties for the content source.
   */
  def mappings(): String

  /**
   * @param fieldname the name of the field.
   * @return true if field exists in mapping, else false.
   */
  def isValid(fieldname: String): Boolean = {
    lazy val schemaMap = Json.parse(mappings)
    (schemaMap \ fieldname \ "type").asOpt[String] match {
      case Some(_) => true
      case None => false
    }
  }
  
  /**
   * @param fieldname the name of the field.
   * @return true if field is declared as multivalued, else false.
   */
  def isMultiValued(fieldname: String): Boolean = {
    lazy val schemaMap = Json.parse(mappings)
    (schemaMap \ fieldname \ "multi_field").asOpt[String] match {
      case Some("yes") => true
      case Some("no") => false
      case None => false
    }
  }
}
