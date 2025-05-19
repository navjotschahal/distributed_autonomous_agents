package com.example.agent

import scala.collection.mutable
import com.example.model.Task
import akka.actor.ActorRef

class AgentRegistry {
  // Updated data structure to store both capability and ActorRef
  private val agents: mutable.Map[String, (AgentCapability, ActorRef)] = mutable.Map()

  def registerAgent(agentId: String, capability: AgentCapability, actorRef: ActorRef): Unit = {
    agents.put(agentId, (capability, actorRef))
  }

  def unregisterAgent(agentId: String): Unit = {
    agents.remove(agentId)
  }

  def getAgentCapability(agentId: String): Option[AgentCapability] = {
    agents.get(agentId).map(_._1)
  }

  def getAgentRef(agentId: String): Option[ActorRef] = {
    agents.get(agentId).map(_._2)
  }

  def listAgents(): Map[String, (AgentCapability, ActorRef)] = {
    agents.toMap
  }

  def getAvailableAgent(task: Task): Option[ActorRef] = {
    agents.find { case (_, (capability, _)) =>
      task.requiredCapabilities.exists(cap => capability.taskTypes.contains(cap))
    }.map { case (_, (_, actorRef)) => actorRef }
  }
}