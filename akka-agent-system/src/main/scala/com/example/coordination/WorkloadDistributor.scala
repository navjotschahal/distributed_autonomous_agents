package com.example.coordination

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import com.example.model.Task
import com.example.agent.AgentRegistry

class WorkloadDistributor(agentRegistry: AgentRegistry) extends Actor with ActorLogging {
  
  def receive: Receive = {
    case task: Task =>
      val agent = selectAgentForTask(task)
      agent ! task
  }

  private def selectAgentForTask(task: Task): ActorRef = {
    // Logic to select an agent based on task requirements and agent capabilities
    agentRegistry.getAvailableAgent(task) match {
      case Some(agent) => agent
      case None => 
        // Handle case when no suitable agent is found
        log.warning(s"No suitable agent found for task ${task.id}")
        context.system.deadLetters
    }
  }
}

object WorkloadDistributor {
  def props(agentRegistry: AgentRegistry): Props = Props(new WorkloadDistributor(agentRegistry))
}