package io.github.morgaroth.utils.akka.http.exception

import spray.json.{DefaultJsonProtocol, JsonFormat, RootJsonFormat}

import scala.language.implicitConversions

case class ExceptionWrapper(
                             detailMessage: String = "",
                             localizedMessage: String = "",
                             cause: Option[ExceptionWrapper] = None,
                             stackTrace: List[StackTraceElementWrapper] = Nil,
                             supressed: List[ExceptionWrapper] = Nil
                             )

case class StackTraceElementWrapper(declaringClass: String,
                                    methodName: String,
                                    fileName: String,
                                    lineNumber: Int)

object StackTraceElementWrapper {
  implicit def wrapStackStraceElement(elem: StackTraceElement): StackTraceElementWrapper = StackTraceElementWrapper(
    elem.getClassName, elem.getFileName,
    elem.getFileName, elem.getLineNumber
  )
}

object ExceptionWrapper {

  import StackTraceElementWrapper.wrapStackStraceElement

  implicit def wrapThrowable(t: Throwable): ExceptionWrapper = new ExceptionWrapper(
    t.getMessage,
    t.getLocalizedMessage,
    if (t.getCause == t || t.getCause == null) None else Some(wrapThrowable(t.getCause)),
    t.getStackTrace.map(wrapStackStraceElement).toList,
    t.getSuppressed.toList.map(wrapThrowable)
  )
}


trait ExceptionJsonProtocol extends DefaultJsonProtocol {
  implicit val stackTraceElemProtocol: RootJsonFormat[StackTraceElementWrapper] = jsonFormat4(StackTraceElementWrapper.apply)
  implicit val exceptionProtocol: JsonFormat[ExceptionWrapper] = lazyFormat {
    jsonFormat(
      ExceptionWrapper.apply,
      "detailMessage", "localizedMessage",
      "cause", "stackTrace", "supressed"
    )
  }
  implicit val rootExceptionProtocol: RootJsonFormat[ExceptionWrapper] = rootFormat(exceptionProtocol)
}

