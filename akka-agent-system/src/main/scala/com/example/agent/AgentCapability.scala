package com.example.agent

case class AgentCapability(processingSpeed: Int, taskTypes: Set[String]) {
  def canHandle(taskType: String): Boolean = taskTypes.contains(taskType)
}