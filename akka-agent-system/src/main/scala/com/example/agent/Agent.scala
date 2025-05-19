package com.example.agent

import akka.actor.{Actor, ActorRef, Props}
import com.example.model.{Message, Task}
import com.example.protocol.MessageProtocol
import akka.actor.ActorLogging


class Agent(val id: String, val capabilities: AgentCapability) extends Actor with ActorLogging {
  
  override def receive: Receive = {
  case task: Task =>
    log.info(s"Agent $id received task: ${task.id} - ${task.description}")
    processTask(task)
    
  case message: Message =>
    log.info(s"Agent $id received message: ${message.content}")
    handleMessage(message)
}

  private def processTask(task: Task): Unit = {
    // Logic to process the task based on agent's capabilities
    println(s"Agent $id processing task: ${task.description}")
    // Simulate task processing
    Thread.sleep(1000)
    sender() ! Message("agent-" + id, sender().path.name, s"Task ${task.id} completed by Agent $id", "TASK_COMPLETE")
  }

  private def handleMessage(message: Message): Unit = {
    // Logic to handle incoming messages from other agents
    println(s"Agent $id received message: ${message.content}")
  }
}

object Agent {
  def props(id: String, capabilities: AgentCapability): Props = Props(new Agent(id, capabilities))
}