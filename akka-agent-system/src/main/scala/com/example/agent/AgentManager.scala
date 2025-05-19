package com.example.agent

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import com.example.model.Task
import scala.collection.mutable

class AgentManager(system: ActorSystem) extends Actor with ActorLogging {
  private val agentRegistry = new AgentRegistry()
  private val agents = mutable.Map[String, ActorRef]()
  
  override def receive: Receive = {
    case task: Task =>
      log.info(s"Received task: ${task.id}")
      distributeTask(task)
      
    case RegisterAgent(id, capabilities, actorRef) =>
      val refToUse = if (actorRef != ActorRef.noSender) actorRef else sender()
      registerAgent(id, capabilities, refToUse)
      
    case UnregisterAgent(id) =>
      unregisterAgent(id)
  }
  
  private def registerAgent(id: String, capabilities: AgentCapability, ref: ActorRef): Unit = {
    // Fix: Use the sender reference if ref is not provided
    val actorRef = if (ref == ActorRef.noSender) sender() else ref
    agentRegistry.registerAgent(id, capabilities, actorRef)
    agents.put(id, actorRef)
    log.info(s"Agent $id registered with capabilities: ${capabilities.taskTypes}")
  }
  
  private def unregisterAgent(id: String): Unit = {
    agentRegistry.unregisterAgent(id)
    agents.remove(id)
    log.info(s"Agent $id unregistered")
  }
  
  private def distributeTask(task: Task): Unit = {
    agentRegistry.getAvailableAgent(task) match {
      case Some(agent) => 
        agent ! task
        log.info(s"Task ${task.id} assigned to an agent")
      case None =>
        log.warning(s"No suitable agent found for task ${task.id}")
    }
  }
}

// Messages for AgentManager
case class RegisterAgent(id: String, capabilities: AgentCapability, agentRef: ActorRef = ActorRef.noSender)
case class UnregisterAgent(id: String)

object AgentManager {
  def props(system: ActorSystem): Props = Props(new AgentManager(system))
}