package getVisibility.calc.api

import akka.actor.Status.Status
import akka.stream.scaladsl.Source
import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}


trait CalculateService extends Service {
  def sayHello : ServiceCall[NotUsed, String]
  def getTime : ServiceCall[NotUsed, Source[String, NotUsed]]
  def serverTime(): ServiceCall[Source[String, NotUsed], Source[String,  NotUsed]]
  def countDown(from:Int): ServiceCall[Int, Source[Int,  NotUsed]]
  def newTime(): ServiceCall[NotUsed, Source[String,  NotUsed]]
  def callActor(): ServiceCall[NotUsed, Source[String,  NotUsed]]
  def scan : ServiceCall[NotUsed, String]

  override def descriptor = {
    import Service._
    named("calculate")
      .withCalls(
        pathCall("/hi", sayHello ),
        pathCall("/timer", getTime),
        pathCall(pathPattern = "/servertime", serverTime()),
        pathCall(pathPattern = "/count/:from", countDown(_)),
        pathCall(pathPattern = "/newTime", newTime()),
        pathCall(pathPattern = "/withactor", callActor()),
          pathCall(pathPattern = "/scan", scan)


      ).withAutoAcl(true)
  }
}


