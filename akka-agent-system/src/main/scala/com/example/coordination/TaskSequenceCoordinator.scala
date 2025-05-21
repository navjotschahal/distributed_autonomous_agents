package com.example.coordination

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.example.model.Task
import scala.collection.mutable

// Messages for task coordination
case object StartProcess
case class TaskComplete(taskId: String, result: Any)

class TaskSequenceCoordinator(processId: String, tasks: List[Task]) extends Actor with ActorLogging {
  private var currentTaskIndex = 0
  private val results = scala.collection.mutable.Map[String, Any]()
  private val agentManager = context.actorSelection("/user/agentManager")
  
  override def receive: Receive = {
    case StartProcess =>
      if (tasks.nonEmpty) {
        log.info(s"Starting process $processId with ${tasks.size} tasks")
        submitNextTask()
      }
      
    case TaskComplete(taskId, result) =>
      log.info(s"Task $taskId completed with result: $result")
      results(taskId) = result
      currentTaskIndex += 1
      
      if (currentTaskIndex < tasks.size) {
        submitNextTask()
      } else {
        log.info(s"Process $processId completed successfully with all tasks")
        // Could report final results here from actual ML model service
      }
  }
  
  private def submitNextTask(): Unit = {
    val task = tasks(currentTaskIndex)
    log.info(s"Submitting task ${task.id} to agent manager")
    agentManager ! task
  }
}

object TaskSequenceCoordinator {
  def props(processId: String, tasks: List[Task]): Props = 
    Props(new TaskSequenceCoordinator(processId, tasks))
}