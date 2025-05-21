package com.example.coordination

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.example.agent.Agent
import com.example.model.Task
import scala.collection.mutable

class TaskCoordinator(agentRegistry: ActorRef) extends Actor {
  private val taskQueue: mutable.Queue[Task] = mutable.Queue()
  
  def receive: Receive = {
    case task: Task =>
      taskQueue.enqueue(task)
      distributeTasks()

    case "CheckStatus" =>
      agentRegistry ! "RequestStatus"
  }

  private def distributeTasks(): Unit = {
    while (taskQueue.nonEmpty) {
      val task = taskQueue.dequeue()
      val availableAgent = findAvailableAgent()
      availableAgent match {
        case Some(agent) =>
          agent ! task
        case None =>
          taskQueue.enqueue(task) // Requeue the task if no agent is available
          return
      }
    }
  }

  private def findAvailableAgent(): Option[ActorRef] = {
    // Logic to find an available agent from the registry
    // This could involve checking the status of agents and their capabilities
    // For simplicity, i assume the first agent is available
    Some(agentRegistry)
  }
}

object TaskCoordinator {
  def props(agentRegistry: ActorRef): Props = Props(new TaskCoordinator(agentRegistry))
}