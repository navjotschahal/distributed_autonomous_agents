package com.example.protocol

import akka.actor.{Actor, ActorRef, Props}
import com.example.model.Message

class MessageRouter extends Actor {
  private var agents: Map[String, ActorRef] = Map()

  def receive: Receive = {
    case RegisterAgent(agentId, agentRef) =>
      agents += (agentId -> agentRef)

    case UnregisterAgent(agentId) =>
      agents -= agentId

    case msg: Message =>
      routeMessage(msg)

    case _ => // Handle unknown messages
  }

  private def routeMessage(msg: Message): Unit = {
    agents.get(msg.receiverId) match {
      case Some(actor) =>
        actor ! msg
      case None =>
        // Handle case where no agent is found for the recipientId need to think about this one
    }
  }
}

object MessageRouter {
  def props: Props = Props(new MessageRouter())
}

case class RegisterAgent(agentId: String, agentRef: ActorRef)
case class UnregisterAgent(agentId: String)