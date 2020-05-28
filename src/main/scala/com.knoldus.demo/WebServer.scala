package com.knoldus.demo

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.FromMessageUnmarshaller
import akka.stream.ActorMaterializer

import scala.io.StdIn
import scala.reflect.ClassTag

case class Student(name:String)
object Student {
  def extractor = {

    def unapply(arg: Student): (Option[String], Option[String]) = {
      val values = arg.name.split(" ")
      if (values.length >= 2) (Some(values(0)), Some(values(1)))
      else (Some(values(0)), None)
    }
  }
}

object WebServer {
  def main(args: Array[String]) {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val route: Route = {
      pathPrefix("a") {
        pathPrefix("b") {
          pathPrefix("Student") {
            id => {
              complete(id.toString)
            }
          }
        }
      }
    }

    // `route` will be implicitly converted to `Flow` using `RouteResult.route2HandlerFlow`
    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
