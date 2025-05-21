package com.example.model

case class AgentStatusUpdate(agentId: String, taskCount: Int, status: String)
case object GetSystemStatus
case object PrintSystemStatus
case class AgentStats(agentId: String, taskCount: Int, status: String, lastUpdate: Long)
case class SystemStatus(agentStats: Map[String, AgentStats])

