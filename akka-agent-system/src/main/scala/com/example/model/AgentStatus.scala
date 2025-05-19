package com.example.model

sealed trait AgentStatus

case object Available extends AgentStatus
case object Busy extends AgentStatus
case object Unavailable extends AgentStatus

case class AgentInfo(status: AgentStatus, workload: Int)