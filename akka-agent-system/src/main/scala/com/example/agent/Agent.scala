package com.example.agent

import akka.actor.{Actor, ActorRef, Props}
import com.example.model.{Message, Task}
import com.example.protocol.MessageProtocol
import akka.actor.ActorLogging
import com.example.coordination.StoreKnowledge
import com.example.coordination.RetrieveKnowledge
import com.example.coordination.{StoreKnowledge, RetrieveKnowledge, KnowledgeResponse}
import scala.concurrent.duration._
import com.example.coordination.TaskComplete
import com.example.model.TaskEnvelope
import com.example.model.AgentStatusUpdate

class Agent(val id: String, val capabilities: AgentCapability) extends Actor with ActorLogging {
  
  override def receive: Receive = {
    case envelope: TaskEnvelope =>
      val task = envelope.task
      log.info(s"Agent $id received task: ${task.id} - ${task.description}")
      processTask(task, envelope.originalSender)
      
    case message: Message =>
      log.info(s"Agent $id received message: ${message.content}")
      handleMessage(message)
      
    // Other message types...
  }
  
  private def processTask(task: Task, replyTo: ActorRef): Unit = {
    log.info(s"Processing task ${task.id}: ${task.description}")
    
    // Update current status
    context.actorSelection("/user/systemMonitor") ! 
      AgentStatusUpdate(id, 1, s"Processing ${task.id}") 
    
    import context.dispatcher
    context.system.scheduler.scheduleOnce(1.second) {
      log.info(s"Task ${task.id} completed by Agent $id")
      
      // Update completed status
      context.actorSelection("/user/systemMonitor") ! 
        AgentStatusUpdate(id, 0, "Idle")
      
      // Send completion to the original sender, not the immediate sender
      replyTo ! TaskComplete(task.id, s"Result for task ${task.id} from agent $id")
    }
  }
  
  private def handleMessage(message: Message): Unit = {
    message.messageType match {
      case "COLLABORATION_REQUEST" =>
        // Handle collaboration request
        log.info(s"Received collaboration request from ${message.senderId}")
        // Send a response back
        sender() ! Message(id, message.senderId, s"Response from agent $id", "COLLABORATION_RESPONSE")
        
      case "COLLABORATION_RESPONSE" =>
        // Handle collaboration response
        log.info(s"Received collaboration response from ${message.senderId}: ${message.content}")
        
      case _ =>
        log.info(s"Received message of type ${message.messageType}")
    }
  }

// Future scope for collaboration functionality on demand
  def collaborate(task: Task, otherAgentIds: Set[String]): Unit = {
    // Request help from other agents
    otherAgentIds.foreach { agentId =>
      val helpRequest = Message(
        id, 
        agentId,
        s"Need assistance with task ${task.id} on ${task.requiredCapabilities.mkString(", ")}", 
        "COLLABORATION_REQUEST"
      )
      context.actorSelection(s"../../../user/$agentId") ! helpRequest
    }
  }

  private def shareKnowledge(key: String, value: Any): Unit = {
    context.actorSelection("/user/knowledgeRepository") ! StoreKnowledge(key, value)
  }

  private def retrieveKnowledge(key: String): Unit = {
    context.actorSelection("/user/knowledgeRepository") ! RetrieveKnowledge(key)
  }
}

object Agent {
  def props(id: String, capabilities: AgentCapability): Props = Props(new Agent(id, capabilities))
}