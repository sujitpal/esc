package esc.index

import org.junit.Test
import scala.io.Source
import java.io.File
import scala.compat.Platform
import java.io.FileFilter
import org.junit.Assert._
//import play.api.libs.ws.WS
import play.libs.WS
import play.api.mvc._
import play.api.mvc.Results._
import play.api.mvc.BodyParsers.parse
import scala.xml.XML

class EnronParserTest {

//  @Test def runParser() = {
//    val parser = new EnronParser
//    val fields = parser.parse(Source.fromFile(
//      "/Users/sujit/Downloads/enron_mail_20110402/maildir/jones-t/sent/2059."))
//    fields.foreach(println(_))
//  }
  
//  @Test def walkDirectory() = {
//    val filter = new EnronFileFilter
//    val files = DirectoryWalker.walk(
//      new File("/Users/sujit/Downloads/enron_mail_20110402/maildir"))
//    files.filter(f => filter.accept(f)).take(10).foreach(println(_))
//  }
//  
//  @Test def runParserWithReflection() = {
//    val parser = Class.forName("esc.index.EnronParser").newInstance().asInstanceOf[Parser]
//    val fields = parser.parse(Source.fromFile(
//      "/Users/sujit/Downloads/enron_mail_20110402/maildir/jones-t/sent/2059."))
//    fields.foreach(println(_))
//  }
  
//  @Test def getPropertiesFromConfigFile() = {
//    val props = DirectoryWalker.properties(new File("conf/indexer.properties"))
//    println(props)
//  }
  
//  @Test def testCreateIndexJson() = {
//    println(DirectoryWalker.createIndexJson("enron", 1, 1))
//  }
//  
//  @Test def testCreateSchemaJson() = {
//    println(DirectoryWalker.createSchemaJson("enron", new EnronSchema))
//  }
    
//  @Test def testEnronSchemaMethods() = {
//    val schema = new EnronSchema
//    assertTrue(schema.isValid("message_id"))
//    assertFalse(schema.isValid("foo"))
//    assertFalse(schema.isMultiValued("message_id"))
//    assertFalse(schema.isMultiValued("foo"))
//    assertTrue(schema.isMultiValued("to"))
//  }

//  @Test def testCreateIndexEntryJson() = {
//    val parser = new EnronParser
//    val fields = parser.parse(Source.fromFile(
//      "/Users/sujit/Downloads/enron_mail_20110402/maildir/jones-t/sent/2059."))
//    println(DirectoryWalker.createIndexEntryJson(fields, new EnronSchema))
//  }

//  @Test def testHttpClient() = {
//    println(DirectoryWalker.getEntry("foo", "bar"))
//  }
  
  @Test def testPlayStyleGet() = {
//    val status = Action {request =>
//      Async {
//        WS.url("http://localhost:9200/_status").get().map {
//          response => Ok((response.json \ "ok").as[String])
//        }
//      }
//    }
    val rsp = WS.url("http://localhost:9200/_status").get.get
    println(rsp.getBody)
  }
}