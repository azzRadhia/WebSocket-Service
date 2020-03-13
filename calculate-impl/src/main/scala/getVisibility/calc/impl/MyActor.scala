package getVisibility.calc.impl

import akka.actor.{Actor, ActorSystem, Props}
case object AskNameMessage
case class PutMessage (msg: String)
class MyActor extends Actor {
  var message: String="start ... "
  def receive = {
    case "hello" => println("hello back at you")
    case PutMessage (msg) => message=message + " "+ msg
    case AskNameMessage => {// respond to the 'ask' request
      sender ! message
      message=""
    }
    case _ => println("huh?")

  }
}
 /* object Main extends App {
    val system = ActorSystem("HelloSystem")
    // default Actor constructor
    val helloActor = system.actorOf(Props[MyActor], name = "helloactor")
    helloActor ! "hello"
    helloActor ! "buenos dias"
  }*/

