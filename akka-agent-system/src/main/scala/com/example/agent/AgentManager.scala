package com.example.agent

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import com.example.model.{Task, TaskEnvelope, QueueStatusUpdate} // Add QueueStatusUpdate import
import scala.collection.mutable
import scala.concurrent.duration._

class AgentManager(system: ActorSystem) extends Actor with ActorLogging {
  private val agentRegistry = new AgentRegistry()
  private val agents = mutable.Map[String, ActorRef]()
  private val taskQueue = mutable.PriorityQueue.empty[TaskEnvelope](Ordering.by[TaskEnvelope, Int](_.task.priority).reverse)
  
  private var totalTasksGenerated = 0
  private val systemMonitor = context.actorSelection("/user/systemMonitor")

  override def receive: Receive = {
    case task: Task =>
      totalTasksGenerated += 1
      log.info(s"Received task: ${task.id}")
      distributeTask(TaskEnvelope(task, sender()))
      
      log.info(s"Queue status: total=$totalTasksGenerated, waiting=${taskQueue.size}")
      systemMonitor ! QueueStatusUpdate(totalTasksGenerated, taskQueue.size)
      
    case RegisterAgent(id, capabilities, actorRef) =>
      val refToUse = if (actorRef != ActorRef.noSender) actorRef else sender()
      registerAgent(id, capabilities, refToUse)
      
    case UnregisterAgent(id) =>
      unregisterAgent(id)

    case ProcessQueue =>
      processQueue()
      systemMonitor ! QueueStatusUpdate(totalTasksGenerated, taskQueue.size)
  }
  
  private def registerAgent(id: String, capabilities: AgentCapability, ref: ActorRef): Unit = {
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
  
  private def distributeTask(envelope: TaskEnvelope): Unit = {
    agentRegistry.getAvailableAgent(envelope.task) match {
      case Some(agent) => 
        agent ! envelope
        log.info(s"Task ${envelope.task.id} assigned to an agent")
        systemMonitor ! QueueStatusUpdate(totalTasksGenerated, taskQueue.size)
      case None =>
        log.warning(s"No suitable agent found for task ${envelope.task.id}")
        taskQueue.enqueue(envelope)
        log.info(s"Task ${envelope.task.id} added to queue. Queue size: ${taskQueue.size}")
        systemMonitor ! QueueStatusUpdate(totalTasksGenerated, taskQueue.size)
        scheduleQueueProcessing()
    }
  }
  
  private def processQueue(): Unit = {
    if (taskQueue.nonEmpty) {
      val envelope = taskQueue.dequeue()
      distributeTask(envelope)
    }
  }
  
  private def scheduleQueueProcessing(): Unit = {
    import context.dispatcher
    context.system.scheduler.scheduleOnce(500.milliseconds, self, ProcessQueue)
  }
}

object AgentManager {
  def props(system: ActorSystem): Props = Props(new AgentManager(system))
}

// Message classes for AgentManager
case class RegisterAgent(id: String, capabilities: AgentCapability, agentRef: ActorRef = ActorRef.noSender)
case class UnregisterAgent(id: String)
case object ProcessQueue
case class QueueStatusUpdate(totalTasksGenerated: Int, tasksWaiting: Int)