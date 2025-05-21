package com.example.agent

import scala.collection.mutable
import com.example.model.Task
import akka.actor.ActorRef

class AgentRegistry {
  private val agents: mutable.Map[String, (AgentCapability, ActorRef)] = mutable.Map()
  private val agentLoad = mutable.Map[String, Int]()

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

  def incrementLoad(agentId: String): Unit = {
    agentLoad.updateWith(agentId) {
      case Some(load) => Some(load + 1)
      case None => Some(1)
    }
  }

  def decrementLoad(agentId: String): Unit = {
    agentLoad.updateWith(agentId) {
      case Some(load) if load > 0 => Some(load - 1)
      case _ => Some(0)
    }
  }

  def getAvailableAgent(task: Task): Option[ActorRef] = {
    val capableAgents = agents.filter { case (_, (capability, _)) =>
      task.requiredCapabilities.exists(cap => capability.taskTypes.contains(cap))
    }
    
    capableAgents.toSeq
      .sortBy { case (id, _) => agentLoad.getOrElse(id, 0) }
      .headOption
      .map { case (id, (_, actorRef)) =>
        incrementLoad(id) 
        actorRef
      }
  }
}