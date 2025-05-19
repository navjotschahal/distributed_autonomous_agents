package com.example.protocol

// MessageProtocol defines the structure of messages exchanged between agents.
sealed trait MessageProtocol

// Request message sent from one agent to another.
case class RequestMessage(taskId: String, payload: String) extends MessageProtocol

// Response message sent back to the requesting agent.
case class ResponseMessage(taskId: String, result: String) extends MessageProtocol

// Status update message to inform about the agent's current state.
case class StatusUpdate(agentId: String, status: String) extends MessageProtocol

// Message for task assignment from the TaskCoordinator to an agent.
case class TaskAssignment(taskId: String, agentId: String) extends MessageProtocol

// Message for notifying task completion.
case class TaskCompletion(taskId: String, agentId: String) extends MessageProtocol

// Message for error reporting.
case class ErrorMessage(taskId: String, error: String) extends MessageProtocol

// Companion object for MessageProtocol to provide utility methods.
object MessageProtocol {
  def isRequest(message: MessageProtocol): Boolean = message match {
    case _: RequestMessage => true
    case _ => false
  }

  def isResponse(message: MessageProtocol): Boolean = message match {
    case _: ResponseMessage => true
    case _ => false
  }

  def isStatusUpdate(message: MessageProtocol): Boolean = message match {
    case _: StatusUpdate => true
    case _ => false
  }
}