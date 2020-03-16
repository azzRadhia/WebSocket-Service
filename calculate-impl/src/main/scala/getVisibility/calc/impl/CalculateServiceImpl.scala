package getVisibility.calc.impl

import java.time.LocalDateTime
import java.util.Calendar

import akka.NotUsed
import akka.actor.Status.Success
import akka.actor.{ActorRef, ActorRefFactory, ActorSystem, Props}
import akka.pattern.ask
import akka.event.Logging.Info
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, RunnableGraph, Sink, Source}
import akka.util.Timeout
import com.lightbend.lagom.scaladsl.api.{Descriptor, ServiceCall}
import getVisibility.calc.api.CalculateService
import play.api.http.websocket.{Message, TextMessage}
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent._
import ExecutionContext.Implicits.global

/**
  * Implementation of the MyprojectService.
  */
class CalculateServiceImpl
  extends CalculateService {
  //for my actor
  val system = ActorSystem("HelloSystem")
  val helloActor = system.actorOf(Props[MyActor], name = "helloactor")

  //for actorRef
  implicit val actorREfFactory=system
  implicit val materializer = ActorMaterializer()


  /*val (actor, source) = Source.actorRef[String](10, akka.stream.OverflowStrategy.dropTail)
    .toMat(BroadcastHub.sink[String])(Keep.both)
    .run()*/
   val source1: Source[String, ActorRef] = Source.actorRef[String](10, akka.stream.OverflowStrategy.dropTail);
  //val source = Source(1 to 10)
  val sink: Sink[String, Source[String, NotUsed]] = BroadcastHub.sink[String];

  // connect the Source to the Sink, obtaining a RunnableGraph
  val runnableGraph: RunnableGraph[(ActorRef, Source[String, NotUsed])] = source1.toMat(sink)(Keep.both);

  // materialize the flow and get the value of the sink
 val (actor, source): (ActorRef, Source[String, NotUsed]) = runnableGraph.run();

  override def sayHello = ServiceCall { _ =>
    Future.successful("Hello1 !")
  }

  override def getTime: ServiceCall[NotUsed, Source[String, NotUsed]] = ServiceCall {
    _ => Future.successful(Source.repeat(time).
      mapMaterializedValue(_ => NotUsed))
  }
 // private def time = Calendar.getInstance.getTime.toString
  private def time = LocalDateTime.now().toString

  override def serverTime(): ServiceCall[Source[String, NotUsed], Source[String, NotUsed]] = ServiceCall { _ =>
    import scala.concurrent.duration._
 // Future(Source.tick(3.seconds, 3.seconds, time).mapMaterializedValue(_ => NotUsed))
  //Future(Source.repeat(NotUsed).map(_ => time))
    Future(Source.tick(3.seconds, 3.seconds, NotUsed).map(_ => time).mapMaterializedValue(_ => NotUsed))


  }
  def countDown(from: Int):ServiceCall[Int, Source[Int,  NotUsed]]  = ServiceCall { _ =>
    Future(Source.unfold(from) { current =>
      if (current == 0) None
      else Some((current - 1, current))
    })
  }

  def newTime():ServiceCall[NotUsed, Source[String,  NotUsed]]  = ServiceCall { _ =>
    Future(Source.unfold(time) { current =>
      Some(time, current)
    })
  }

  /*Actor Part/

   */

  //This function asks my actor to get messages
  private def askActor = {
    implicit val timeout = Timeout(5.seconds)
    val future = helloActor  ? AskNameMessage
    Await.result(future, timeout.duration).asInstanceOf[String]
  }

  /*THis websocket retrieves messages from actor and send response to the customer*/
  def callActor():ServiceCall[NotUsed, Source[String,  NotUsed]]  = ServiceCall { _ =>

    //with actor ref
   /* val wsHandler: Flow[Message, Message, NotUsed] = Flow[Message]
      .mapConcat(_ => Nil)
      .merge(source)
      .map(l => TextMessage(l.toString))*/

    Future(source)

  // with my actor
 // Future(Source.tick(3.seconds, 3.seconds, NotUsed).map(_ => "//"+askActor).mapMaterializedValue(_ => NotUsed))
  }

  /**This service sends messages to teh actor */
def scan = ServiceCall { _ =>

  var x=0
  var deadline = 5.seconds.fromNow

  while (x<=5  ) {
    if ( ! deadline.hasTimeLeft()){
   // actor ! PutMessage("my message number "+x)
      actor ! ("This is message number "+x)
      x=x+1
      deadline = 3.seconds.fromNow
    }
  }
  /*actor ! "msg1"
  actor ! "msg2"*/
  Future("")
}


}