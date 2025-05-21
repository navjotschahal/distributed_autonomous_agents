package com.example.demo

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import scala.collection.mutable
import com.example.model.{AgentStatusUpdate, GetSystemStatus, PrintSystemStatus, AgentStats, SystemStatus, QueueStatusUpdate} // Added QueueStatusUpdate
import javax.swing.{JFrame, JTextArea, JScrollPane}
import javax.swing.WindowConstants
import java.awt.{Font, Dimension}
import javax.swing.SwingUtilities

// Create a simple system monitor
class SystemMonitor(system: ActorSystem) extends Actor with ActorLogging {
  private val agentStats = mutable.Map[String, AgentStats]()
  private val statusDisplay = new SystemStatusWindow("Akka Agent System Monitor")
  
  // Add these variables to track task queue stats
  private var totalTasksGenerated = 0
  private var tasksWaiting = 0
  
  override def receive: Receive = {
    case AgentStatusUpdate(agentId, taskCount, status) =>
      agentStats.put(agentId, AgentStats(agentId, taskCount, status, System.currentTimeMillis()))
      
    // Add this case to handle queue status updates
    case QueueStatusUpdate(total, waiting) =>
      log.debug(s"Received QueueStatusUpdate($total, $waiting)")
      totalTasksGenerated = total
      tasksWaiting = waiting
      
    case GetSystemStatus =>
      sender() ! SystemStatus(agentStats.toMap)
      
    case PrintSystemStatus =>
      printStatus()
  }
  
  private def printStatus(): Unit = {
    // Format status as a string
    val sb = new StringBuilder()
    sb.append("\n===== SYSTEM STATUS =====\n")
    
    // Add task queue information
    sb.append(s"Total tasks generated: $totalTasksGenerated\n")
    sb.append(s"Tasks waiting in queue: $tasksWaiting\n")
    sb.append(s"Active agents: ${agentStats.size}\n")
    sb.append("Agent Status:\n")
    
    agentStats.values.toSeq.sortBy(_.agentId).foreach { stats =>
      val timestamp = java.time.format.DateTimeFormatter
        .ofPattern("HH:mm:ss")
        .format(java.time.Instant.ofEpochMilli(stats.lastUpdate)
          .atZone(java.time.ZoneId.systemDefault())
          .toLocalDateTime())
          
      sb.append(f"  - ${stats.agentId}%-15s | Tasks: ${stats.taskCount}%2d | Status: ${stats.status}%-10s | Updated: $timestamp\n")
    }
    
    sb.append("========================\n")
    
    // Update the status window
    statusDisplay.updateStatus(sb.toString())
    
    // Also log to console for redundancy
    log.info("System status updated in monitor window")
  }
  
  override def preStart(): Unit = {
    super.preStart()
    statusDisplay.initialize()
  }
  
  override def postStop(): Unit = {
    statusDisplay.close()
    super.postStop()
  }
}

class SystemStatusWindow(title: String) {
  private val frame = new JFrame(title)
  private val textArea = new JTextArea()
  
  def initialize(): Unit = {
    SwingUtilities.invokeLater(() => {
      textArea.setEditable(false)
      textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14))
      
      val scrollPane = new JScrollPane(textArea)
      frame.add(scrollPane)
      
      frame.setSize(new Dimension(800, 600))
      frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE) // Don't close the application
      frame.setVisible(true)
      
      updateStatus("System monitor initialized. Waiting for agent data...")
    })
  }
  
  def updateStatus(status: String): Unit = {
    SwingUtilities.invokeLater(() => {
      textArea.setText(status)
    })
  }
  
  def close(): Unit = {
    SwingUtilities.invokeLater(() => {
      frame.dispose()
    })
  }
}


