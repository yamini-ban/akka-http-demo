package com.knoldus.demo

import akka.actor.{Actor, ActorLogging, Props}

object PrinterActor {
  def props: Props = {
    Props(new PrinterActor)
  }
}

class PrinterActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case msg => println("hello")
  }
}
