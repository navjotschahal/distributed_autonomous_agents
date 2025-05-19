package com.example.model

case class Task(id: String, description: String, requiredCapabilities: Set[String], priority: Int) {
  def isHighPriority: Boolean = priority > 5
}