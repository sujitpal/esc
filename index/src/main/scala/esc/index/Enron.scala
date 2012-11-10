package esc.index

import scala.io.Source
import scala.collection.immutable.HashMap
import java.io.FileFilter
import java.io.File
import java.util.Date
import java.text.SimpleDateFormat

/**
 * User-configurable classes for the Enron data. These are
 * the classes that will be required to be supplied by a user
 * for indexing a new data source. 
 */

class EnronParser extends Parser {

  override def parse(source: Source): Map[String,String] = {
    parse0(source.getLines(), HashMap[String,String](), false)
  }
  
  def parse0(lines: Iterator[String], map: Map[String,String], startBody: Boolean): Map[String,String] = {
    if (lines.isEmpty) map
    else {
      val head = lines.next()
      if (head.trim.length == 0) parse0(lines, map, true)
      else if (startBody) {
        val body = map.getOrElse("body", "") + "\n" + head
        parse0(lines, map + ("body" -> body), startBody)
      } else {
        val split = head.indexOf(':')
        if (split > 0) {
          val kv = (head.substring(0, split), head.substring(split + 1))
          val key = kv._1.map(c => if (c == '-') '_' else c).trim.toLowerCase
          val value = kv._1 match {
            case "Date" => formatDate(kv._2.trim)
            case _ => kv._2.trim
          }
          parse0(lines, map + (key -> value), startBody)
        } else parse0(lines, map, startBody)
      }
    }
  }
  
  def formatDate(date: String): String = {
    lazy val parser = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss")
    lazy val formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    formatter.format(parser.parse(date.substring(0, date.lastIndexOf('-') - 1)))
  }
}

class EnronFileFilter extends FileFilter {
  override def accept(file: File): Boolean = {
    file.getAbsolutePath().contains("/sent/")
  }
}

class EnronSchema extends Schema {
  override def mappings(): String = """{
    "message_id": {"type": "string", "index": "not_analyzed", "store": "yes"},
    "from": {"type": "string", "index": "not_analyzed", "store": "yes"},
    "to": {"type": "string", "index": "not_analyzed", "store": "yes", "multi_field": "yes"},
    "x_cc": {"type": "string", "index": "not_analyzed", "store": "yes", "multi_field": "yes"},
    "x_bcc": {"type": "string", "index": "not_analyzed", "store": "yes", "multi_field": "yes"},
    "date": {"type": "date", "index": "not_analyzed", "store": "yes"},
    "subject": {"type": "string", "index": "analyzed", "store": "yes"},
    "body": {"type": "string", "index": "analyzed", "store": "yes"}
  }"""
}