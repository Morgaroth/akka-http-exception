package io.github.morgaroth.utils.akka.http.exception

import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

import spray.json._

class ExceptionJsonProtocolTest extends WordSpec with Matchers {

  trait ctx {
    val t = Try(throw new IllegalArgumentException("fdsf")).failed.get
    val underTest = new ExceptionJsonProtocol {}.rootExceptionProtocol
  }

  "Exception json protocol" should {
    "marshal correctly" in new ctx {
      val a = underTest.write(t).prettyPrint.parseJson
      val fields = a.asJsObject.fields
      fields.keys should (contain("stackTrace") and contain("detailMessage"))
    }
  }
}
