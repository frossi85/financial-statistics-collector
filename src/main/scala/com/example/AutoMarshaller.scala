package com.example


import de.heikoseeberger.akkahttpjson4s._
import org.json4s.{DefaultFormats, jackson}

trait AutoMarshaller extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats

  //implicit def json4sFormats = DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all ++ JavaTypesSerializers.all
}
